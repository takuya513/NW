package quickSort;

import java.util.concurrent.ForkJoinPool;

public class ForkJoinQuickSort<E extends Comparable> {
	ForkJoinPool pool;
	E[] array;
	public ForkJoinQuickSort() {

	}

	public long sort(E[] array){
		this.array = array;
		pool = new ForkJoinPool();
		long start = System.nanoTime();
		pool.invoke(new ForkJoinTask(array,0,array.length-1));
		long stop = System.nanoTime();

		return stop - start;
	}


}
