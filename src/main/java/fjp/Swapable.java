package fjp;

public interface Swapable {
    default void swap(Object[] array, int index1, int index2) {
        if (index1 < 0 || index1 > array.length - 1 || index2 < 0 || index2 > array.length - 1)
            throw new IllegalArgumentException();
        if (index1 == index2) return;
        Object temp = array[index1];
        array[index1] = array[index2];
        array[index2] = temp;
    }
}
