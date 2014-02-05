package quickSort;

import tools.Sortable;

public class QuickSortTask<E extends Comparable> extends QuickSort<E> implements Runnable {
	protected int left;
	protected int right;
	public QuickSortTask(E[] array,int left,int right){
		//super(array);
		this.array = array;
		this.left = left;
		this.right = right;
	}

	@Override
	public void run() {
		int i;

		if(right > left) {
			i = partition(left,right);
			quickSort(left, i - 1);
			quickSort(i + 1 , right);
		}
	}
}
