package fjp;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FJPRecursiveAction {

    public static void main(String[] args) throws InterruptedException {
        Integer[] numbers = ThreadLocalRandom.current().ints(50, 0, 100).boxed().toArray(Integer[]::new);
        System.out.println("Before sort: " + Arrays.toString(numbers));
        ForkJoinPool executor = new ForkJoinPool(3);

        executor.execute(new FJPMergeSort(numbers));
        executor.awaitTermination(300, TimeUnit.MILLISECONDS);

        System.out.println("After sort: " + Arrays.toString(numbers));
    }
}

class FJPMergeSort<E extends Comparable<E>> extends RecursiveAction implements MergeSortArrayAlg, Swapable {

    private final static int THRESHOLD = 2;
    private final E[] data;
    private final int from;
    private final int to;

    public FJPMergeSort(E[] data, int from, int to) {
        this.data = data;
        this.from = from;
        this.to = to;
    }

    public FJPMergeSort(E[] data) {
        this(data, 0, data.length - 1);
    }

    @Override
    protected void compute() {
        System.out.println(Thread.currentThread().getName() + " get task from " + from + " to " + to);
        if (to - from < THRESHOLD) {
            if (data[from].compareTo(data[to]) > 0) {
                swap(data, from, to);
            }
        } else {
            int mid = from + ((to - from) >>> 1);
            FJPMergeSort<E> left = new FJPMergeSort<>(data, from, mid);
            FJPMergeSort<E> right = new FJPMergeSort<>(data, mid + 1, to);

            invokeAll(left, right);
            merge(data, from, mid, to);
        }
    }
}
