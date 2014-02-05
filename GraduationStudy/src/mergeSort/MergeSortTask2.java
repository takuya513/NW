package mergeSort;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import quickSort.ForkJoinTask;
import tools.MyArrayUtil;


class MergeSortTask2 extends RecursiveAction {
	 private final static int INSERTIONSORT_THRESHOLD = 7;
    final Object[] src;
    final Object[] dest;
    int low;
    int high;

    public MergeSortTask2(Object[] src, Object[] dest, int low, int high) {
        this.src = src;
        this.dest = dest;
        this.low = low;
        this.high = high;
    }

    private static void swap(Object[] x, int a, int b) {
        Object t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    protected void compute() {
        int length = high - low;

        if (length <= INSERTIONSORT_THRESHOLD) {
            for (int i = low; i < high; i++) {
                for (int j = i; j > low && ((Comparable) src[j - 1]).compareTo(src[j]) > 0; j--) {
                    swap(src, j, j - 1);
                }
            }
            return;
        }

        int destLow = low;
        int destHigh = high;
        //int mid = (low + high) >>> 1;
        int mid = (low + high) / 2;
        MergeSortTask2 left = new MergeSortTask2(src, dest, low, mid);
        left.fork();

        MergeSortTask2 right = new MergeSortTask2(src, dest, mid, high);
        right.compute();
        left.join();

        if (((Comparable) src[mid - 1]).compareTo(src[mid]) <= 0) {
            return;
        }

        for (int i = destLow, p = low, q = mid; i < destHigh; i++) {
            if (q >= high || p < mid && ((Comparable) src[p]).compareTo(src[q]) <= 0) {
                dest[i] = src[p++];
            } else {
                dest[i] = src[q++];
            }
        }

        System.arraycopy(dest, low, src, low, length);
    }
}
