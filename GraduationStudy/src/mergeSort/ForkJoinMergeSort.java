package mergeSort;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import quickSort.ForkJoinTask;
import tools.MyArrayUtil;

public class ForkJoinMergeSort<E extends Comparable> {

	//E[] array;


	public ForkJoinMergeSort(){}


	public long sort(E[] array){
		//E[] array = array;
		//E[] dest = array.clone();
		 E[] dest =Arrays.copyOfRange(array, 0, array.length);
		//Object dest = new Object[array2.length + 1];
		ForkJoinPool pool = new ForkJoinPool();

		MergeSortTask task = new MergeSortTask(array,dest,0,array.length);
		long start = System.nanoTime();
		pool.invoke(task);
		long stop = System.nanoTime();

		return stop - start;
	}

	 static class MergeSortTask<E extends Comparable> extends RecursiveAction {
	        final E[] src;
	        final E[] dest;
	        int low;
	        int high;

	        public MergeSortTask(E[] src, E[] dest, int low, int high) {
	            this.src = src;
	            this.dest = dest;
	            this.low = low;
	            this.high = high;
	        }

	        private  void swap(E[] x, int a, int b) {
	            E t = x[a];
	            x[a] = x[b];
	            x[b] = t;
	        }

	        protected void compute() {
	            int length = high - low;

	            //バブルソート
	            if (length <= 7) {
	                for (int i = low; i < high; i++) {
	                    for (int j = i; j > low && dest[j - 1].compareTo(dest[j]) > 0; j--) {
	                        swap(dest, j, j - 1);
	                    }
	                }
	                return;
	            }

	            int destLow = low;
	            int destHigh = high;
	            int mid = (low + high) >>> 1;
	            //int mid = (low + high) / 2;

	            MergeSortTask left = new MergeSortTask(dest, src, low, mid);
	            left.fork();

	            MergeSortTask right = new MergeSortTask(dest, src, mid, high);

	            right.compute();
	            left.join();

	            if (src[mid - 1].compareTo(src[mid]) <= 0) {
	                System.arraycopy(src, low, dest, destLow, length);
	                return;
	            }

	            for (int i = destLow, p = low, q = mid; i < destHigh; i++) {
	                if (q >= high || p < mid && src[p].compareTo(src[q]) <= 0) {
	                    dest[i] = src[p++];
	                } else {
	                    dest[i] = src[q++];
	                }
	            }

	        }
	    }
}
