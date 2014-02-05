package quickSort;

import tools.Sortable;

/*
 * ��̃X���b�h�̂ݎg�p
 */
public class QuickSort2<E extends Comparable> extends QuickSort<E> {

	public QuickSort2(){
	}


	@Override
	public void quickSort(int left,int right){

		int i = partition(left,right);
		Thread leftSort = new Thread(new QuickSortTask<E>(array,left,i-1));
		Thread rightSort = new Thread(new QuickSortTask<E>(array,i+1,right));
		leftSort.start();
		rightSort.start();

//		System.out.println("���݃A�N�e�B�u�ȃX���b�h���@�F "+leftSort.activeCount());
		try {
			leftSort.join();
			rightSort.join();
		} catch (InterruptedException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

}
