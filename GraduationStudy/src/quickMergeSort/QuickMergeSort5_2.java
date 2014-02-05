package quickMergeSort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import tools.MyArrayUtil;
import tools.TestTools;
/*
 * QuickMergeSort5�̃N�C�b�N�\�[�g�̕�����Arrays.sort��
 */
public class QuickMergeSort5_2<E extends Comparable> {


	ExecutorService executor;
	int threadsNum, arrayLength, sortSection, left, right;
	int restPivot; //�}�[�W����Ƃ��ɗ]���������̎d�؂�
	LinkedList<Callable<Object>> workers;

	TestTools tt;

	public QuickMergeSort5_2(){
		threadsNum = Runtime.getRuntime().availableProcessors();

		executor = Executors.newFixedThreadPool(threadsNum);
		workers = new LinkedList<Callable<Object>>();
	}

	public long sort(E[] array){
		E[] buff = array.clone();
		arrayLength = array.length;

		sortSection = arrayLength / (threadsNum * 2);
		left = 0;  right = sortSection - 1;
		restPivot = -1;

		long start = System.nanoTime();
		try {
			//�e�敪���N�C�b�N�\�[�g
			parallelQuickSort(array);

			//�敪���ƂɃ\�[�g���ꂽ�Ƃ�����}�[�W�������Ă���
			parallelMergeSort(array,buff);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long end = System.nanoTime();
		return end - start;


	}

	private void parallelQuickSort(E[] array){
		//�N�C�b�N�\�[�g������
		while(right < arrayLength-1){
			workers.add(Executors.callable(new QuickSortWorker(array,left,right)));
			left = right + 1;
			right =  sortSection + right;
		}
		//�Ō�̋敪�������ʂɏ�������
		workers.add(Executors.callable(new QuickSortWorker(array,left,arrayLength-1)));
		restPivot = left;    //�]�����Ƃ���̎d�؂�͓Ǝ��̂��̂��K�v�Ȃ̂ŕۑ����Ă���

		try {
			executor.invokeAll(workers);  //workers�̎d�������s���A�I���܂őҋ@
		} catch (InterruptedException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	private void parallelMergeSort(E[] array,E[] buff) throws InterruptedException {
		sortSection = sortSection * 2 ;  //��x�Ƀ}�[�W����͈�

		while(sortSection <= arrayLength - 1){
			workers.clear();
			left = 0;  right = sortSection - 1;

			while(right < arrayLength-1){
				workers.add(Executors.callable(new MergeSortWorker(array,buff,left,right)));
				left = right + 1;
				right = right + sortSection;

				if(right >= arrayLength-1){  //�]�蕔���̏��� left < arrayLength
					if((arrayLength - left) <= sortSection/2){  //�����͈͂̃\�[�g�h�~
						restPivot = left;  //�]�����Ƃ���̎d�؂�͓Ǝ��̂��̂��K�v�Ȃ̂ŕۑ����Ă���
						break;
					}

					workers.add(Executors.callable(new MergeSortWorker(array,buff,left,restPivot-1,arrayLength - 1)));
					restPivot = left;	//�]�����Ƃ���̎d�؂�͓Ǝ��̂��̂��K�v�Ȃ̂ŕۑ����Ă���
					break;
				}
			}

			executor.invokeAll(workers);   ////workers�̎d�������s���A�I���܂őҋ@
			sortSection = sortSection * 2;	//�}�[�W����敪���g�傷��

			threadsNum = threadsNum/2; //�s�K�v�ȃX���b�h�����炵�Ă���
			((ThreadPoolExecutor)executor).setCorePoolSize(threadsNum);
		}

		executor.shutdown();
		//�Ō��merge
		merge(array,buff,0,restPivot-1,arrayLength - 1);
	}

	@SuppressWarnings("unchecked")
	public void merge(E[] array,E[] buff,int left,int mid,int right){
	    if (array[mid].compareTo(array[mid+1]) <= 0) {
            return;
        }



		for (int i = left, p = left, q = mid+1; i <= right; i++) {
			if (q > right || p <= mid && array[p].compareTo(array[q]) <= 0) {
				buff[i] = array[p++];
			} else {
				buff[i] = array[q++];
			}
		}
		System.arraycopy(buff, left, array, left, right-left+1);
	}
	class MergeSortWorker implements Runnable{
		int left,right,mid;
		E[] buff;
		E[] array;
		public MergeSortWorker(E[] array,E[] buff,int left,int right){
			this.left = left;
			this.right = right;
			mid = (left + right) / 2;
			this.buff = buff;
			this.array = array;
		}

		public MergeSortWorker(E[] array,E[] buff,int left,int mid,int right){
			this.left = left;
			this.right = right;
			this.mid = mid;
			this.buff = buff;
			this.array = array;
		}
		public void run(){
			merge(array,buff,left,mid,right);
		}

		@SuppressWarnings("unchecked")
		public void merge(E[] array,E[] buff,int left,int mid,int right){
		    if (array[mid].compareTo(array[mid+1]) <= 0) {
	            return;
	        }



			for (int i = left, p = left, q = mid+1; i <= right; i++) {
				if (q > right || p <= mid && array[p].compareTo(array[q]) <= 0) {
					buff[i] = array[p++];
				} else {
					buff[i] = array[q++];
				}
			}
			System.arraycopy(buff, left, array, left, right-left+1);
		}

	}

	class QuickSortWorker implements Runnable {
		int left,right;
		E[] array;
		public QuickSortWorker(E[] array,int left,int right){
			this.left = left;
			this.right = right;
			this.array = array;
		}

		public void run() {
			Arrays.sort(array,left,right+1);
		}
	}




//	@SuppressWarnings("unchecked")
//	public int partition(int left,int right){
//		int i = left - 1, j = right;
//
//		E pivot  = array[right];
//		while(true){
//			do{
//				i++;
//			}while(array[i].compareTo(pivot) < 0);
//			do{
//				j--;
//				if(j < left) break;
//			}while(pivot.compareTo(array[j]) < 0);
//			if(i >= j) break;
//			swap(i,j);
//		}
//
//		swap(i,right);
//		return i;
//	}
//
//	public void swap(int i,int j){
//		E temp = array[i];
//		array[i] = array[j];
//		array[j] = temp;
//	}

//	@SuppressWarnings("unchecked")
//	public void insertSort(int left,int right){
//		int i,j;
//		E temp;
//		for(i = left + 1; i <=right;i++) {
//			temp = array[i];
//			j = i;
//			while(j > left && temp.compareTo(array[j-1])< 0){
//				array[j] = array[j-1];
//				j--;
//			}
//			array[j] = temp;
//		}
//	}

//	@SuppressWarnings("unchecked")
//	public void quickSort(int left,int right){
//		int i;
//		if(right <= left) return;
//
//		if(right <= left + SMALL)
//			insertSort(left,right);
//		else{
//
//			swap((left + right) / 2,right - 1);
//			if(array[right - 1].compareTo(array[left]) < -1)
//				swap(right-1,left);
//			if(array[right].compareTo(array[left]) < -1)
//				swap(right,left);
//			if(array[right].compareTo(array[right-1]) < -1)
//				swap(right,right);
//
//			i = partition(left,right);
//			quickSort(left, i - 1);
//			quickSort(i + 1 , right);
//		}
//	}

}
