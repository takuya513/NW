package mergeSort;

import java.util.LinkedList;

import sort.Sort;

public class MergeSort<E extends Comparable> implements Sort<E>{
	E[] array;
	LinkedList<E> buff;
	public MergeSort(){
		buff = new LinkedList<E>();
	}

	public void sort(E[] array){
		this.array = array;
		partition(0,array.length - 1);
	}

	public void partition(int left,int right){
		int mid = (left + right) / 2;

		if(right <= left)
			return;

		partition(left,mid);
		partition(mid + 1, right);

		merge(left,mid,right);
	}

	@SuppressWarnings("unchecked")
	public void merge(int left,int mid,int right){
		int i = left,j = mid + 1,k = 0;
		Object[] buff = new Object[right - left+2];

		while(i <= mid && j <= right) {
			if(array[i].compareTo(array[j]) < 0)
				buff[k++] = array[i++];
			else
				buff[k++] = array[j++];
		}

		while(i <= mid)
			buff[k++] = array[i++];
		while(j <= right)
			buff[k++] = array[j++];
		for(i = left;i <= right; i++)
			array[i] = (E) buff[i - left];
	}

}
