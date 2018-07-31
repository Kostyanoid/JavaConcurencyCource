package fjp;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ThreadLocalRandom;

public class FPJRecursiveTask {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Integer[] numbers = ThreadLocalRandom.current().ints(100, 0, 1000).boxed().toArray(Integer[]::new);
        System.out.println(Arrays.toString(numbers));
        ForkJoinPool executor = new ForkJoinPool(3);
        System.out.println("Max is: " + executor.submit(new FJPMaximazer(numbers)).get());
    }
}

class FJPMaximazer<E extends Comparable<E>> extends RecursiveTask<E> {

    private final static int THRESHOLD = 10;
    private final E[] data;
    private final int from;
    private final int to;

    public FJPMaximazer(E[] data) {
        this(data, 0, data.length - 1);
    }

    public FJPMaximazer(E[] data, int from, int to) {
        this.data = data;
        this.from = from;
        this.to = to;
    }

    @Override
    protected E compute() {
        System.out.println(Thread.currentThread().getName() + " get task from " + from + " to " + to);
        if (to - from <= THRESHOLD) {
            return Arrays.stream(data, from, to + 1).max(new Comparator<E>() {
                @Override
                public int compare(E o1, E o2) {
                    return o1.compareTo(o2);
                }
            }).orElseThrow(IllegalStateException::new);
        } else {
            int mid = from + ((to - from) >>> 1);
            FJPMaximazer<E> left = new FJPMaximazer<>(data, from, mid);
            FJPMaximazer<E> right = new FJPMaximazer<>(data, mid + 1, to);

            invokeAll(left, right);
            E rightResult = right.join();
            E leftResult = left.join();
            return rightResult.compareTo(leftResult) > 0 ? rightResult : leftResult;
        }
    }
}
