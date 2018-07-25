package locks;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ArrayBlockingStack<E> {

    private final Object[] elements;
    private final Lock lock;
    private final Condition notEmpty;
    private final Condition notFull;
    private volatile int headIndex;

    public ArrayBlockingStack(int size, boolean fair) {
        if (size < 1) throw new IllegalArgumentException("Size can't be less then 1!");
        elements = new Object[size];
        lock = new ReentrantLock(fair);
        headIndex = 0;
        notEmpty = lock.newCondition();
        notFull = lock.newCondition();
    }

    public ArrayBlockingStack(int size) {
        this(size, false);
    }

    /**
     * Put a new element in the stack.
     * Return <code>false</code> if stack was full before pushing.
     * @param element - a new element is putting in the stack
     * @return true - if an element was put successfully, false - otherwise.
     */
    public boolean offer(E element) {
        Objects.requireNonNull(element);
        lock.lock();
        try {
            if (isFull()) {
                return false;
            }
            addElementToStack(element);
        } finally {
            lock.unlock();
        }

        return true;
    }

    /**
     * Trying to put a new element in the stack. If the stack is full wait for timeout.
     * If the stack will be full after waiting, return false.
     * @param element - a new element is putting in the stack
     * @return true - if an element was put successfully, false - otherwise.
     */
    public boolean offer(E element, long timeout, TimeUnit unit) throws InterruptedException {
        Objects.requireNonNull(element);
        long nanos = unit.toNanos(timeout);
        lock.lockInterruptibly();
        try {
            while (isFull()) {
                if (nanos <= 0) {
                    return false;
                }
                nanos = notFull.awaitNanos(nanos);
            }
            addElementToStack(element);
        } finally {
            lock.unlock();
        }

        return true;
    }

    /**
     * Push a new element in the stack. Throws an IllegalStateException
     * if stack was full before pushing.
     * @param element - a new element is pushing in the stack
     * @return true - if an element was put successfully.
     * @throws IllegalStateException if the stack was full.
     */
    public boolean push(E element) throws IllegalStateException {
        if (!offer(element)) {
            throw new IllegalStateException("Stack is full!");
        }
        return true;
    }

    /**
     * Get and remove an element from the head of stack.
     * Wait while a new element will be pushed into the stack if stack is empty.
     * @return an element from the head.
     */
    public E pop() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (isEmpty()) {
                notEmpty.await();
            }
            return receiveElementFromStack();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Receive an element from the head of stack without removing.
     * Return null if the stack is empty.
     * @return an element from the head or null.
     */
    public E peek() {
        lock.lock();
        try {
            if (isEmpty()) {
                return null;
            }
            return (E) elements[headIndex - 1];
        } finally {
            lock.unlock();
        }
    }

    private void addElementToStack(E element) {
        elements[headIndex] = element;
        headIndex++;
        if (headIndex == 1)
            notEmpty.signal();
    }

    private E receiveElementFromStack() {
        E element = (E) elements[headIndex];
        elements[headIndex] = null;
        headIndex--;
        if (headIndex == elements.length - 1) {
            notFull.signal();
        }
        return element;
    }

    private boolean isFull() {
        return headIndex == elements.length;
    }

    private boolean isEmpty() {
        return headIndex == 0;
    }
}
