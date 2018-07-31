package fjp;

public interface MergeSortArrayAlg {

    default void merge(Comparable[] array,
                       int firstIndex,
                       int medIndex,
                       int lastIndex) {

        if (firstIndex > medIndex || medIndex + 1 > lastIndex) throw new IllegalArgumentException();
        if (firstIndex < 0 || lastIndex >= array.length) throw new IndexOutOfBoundsException();

        Comparable[] mergedArray = new Comparable[lastIndex - firstIndex + 1];
        int mergedArrayIndex = 0;
        int firstArrayPointer = firstIndex;
        int secondArrayPointer = medIndex + 1;
        while (firstArrayPointer <= medIndex && secondArrayPointer <= lastIndex) {
            if (array[firstArrayPointer].compareTo(array[secondArrayPointer]) > 0) {
                mergedArray[mergedArrayIndex] = array[secondArrayPointer];
                secondArrayPointer++;
            } else {
                mergedArray[mergedArrayIndex] = array[firstArrayPointer];
                firstArrayPointer++;
            }
            mergedArrayIndex++;
        }

        //выполнится только один из циклов
        for (int i = firstArrayPointer; i <= medIndex; i++) {
            mergedArray[mergedArrayIndex++] = array[i];
        }

        for (int i = secondArrayPointer; i <= lastIndex; i++) {
            mergedArray[mergedArrayIndex++] = array[i];
        }

        System.arraycopy(mergedArray, 0, array, firstIndex, mergedArray.length);
    }
}
