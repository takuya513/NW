package newSort;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import newSort.NewSort3.SortBlock;

import quickSort.QuickSort;
import tools.MyArrayUtil;
import tools.TestTools;

public class NewSort4 <E extends Comparable> extends QuickSort<E>{
	ExecutorService executor;
	int threadsNum,arrayLength,pos,first;
	List<Callable<Object>> workers;
	List<SortBlock> works;
	BlockingQueue<SortBlock> quickSortTasks;
	boolean endArray = false;
	TestTools tt;
	long start,stop,start2,stop2,totalAdd=0,totalGet=0;


	public NewSort4() {
		threadsNum = Runtime.getRuntime().availableProcessors();
		tt = new TestTools();
	}


	@SuppressWarnings("unchecked")
	public void sort(E[] array){
		this.array = array;
		arrayLength = array.length;
		executor = Executors.newFixedThreadPool(threadsNum);
		workers = new ArrayList<Callable<Object>>(threadsNum);
		works = new ArrayList<SortBlock>();
		//works = new SortBlock[arrayLength];
		//quickSortTasks = new LinkedBlockingQueue<SortBlock>();
		pos = 0;

		try {
			start = System.currentTimeMillis();
			while(!endArray){
				if(array[pos].compareTo(array[pos+1]) <= 0)
					ascendingOrder();
				else
					descendindOrder();

				pos++;  //���̐��𒲂ׂ�

				if(pos+1 > arrayLength-1){
					works.add(new SortBlock(pos,pos));
					break;
				}
			}


			executor.invokeAll(workers);


			stop = System.currentTimeMillis();
			//�}�[�W���郁�\�b�h

			start2 = System.currentTimeMillis();
			parallelMergeSort();

			executor.invokeAll(workers);
			stop2 = System.currentTimeMillis();
		}catch (InterruptedException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		executor.shutdown();

//		System.out.println("first : "+(stop - start));
//		System.out.println("second : "+(stop2 - start2));
//		System.out.println("totalGet : "+(int)totalGet);
//		System.out.println("totalAdd : "+(int)totalAdd);
	}

	//�������\�b�h
	@SuppressWarnings("unchecked")
	public void ascendingOrder(){
		first = pos;
		while(array[pos].compareTo(array[pos+1]) <= 0){
			if(pos+1 >= arrayLength-1){
				works.add(new SortBlock(first,pos+1));
				endArray = true;
				return;
			}
			pos++;
		}
		works.add(new SortBlock(first,pos));
		return;
	}

	//�~�����\�b�h
	@SuppressWarnings("unchecked")
	public void descendindOrder(){
		first = pos;

		while(array[pos].compareTo(array[pos+1]) > 0){
			if(pos+1 >= arrayLength-1){
				workers.add(Executors.callable(new QuickSortWorker(first,pos+1)));
				works.add(new SortBlock(first,pos+1));
				endArray = true;
				return;
			}
			pos++;
		}

		if((pos - first) < 10){   //�X�V
			quickSort(first,pos);
		}else
			workers.add(Executors.callable(new QuickSortWorker(first,pos)));//�C���A�����ƌ����悭������

		works.add(new SortBlock(first,pos));
		return;
	}

	//�}�[�W���镔��
	public void parallelMergeSort(){
		int workSize = 0,tempSize = 0;
		int worksPointer = 0;
		SortBlock block1,block2;
		//System.out.println("workSize : "+works.size());
		while(true){
			workSize = works.size() - tempSize;  //�g����work�͂�������K�v�͂Ȃ����߃J�b�g����
			tempSize = workSize + tempSize;   //�g����work�̃T�C�Y��ۑ�

			while(workSize > 1){
				//���works�����o���Aleft,right�����o��
				long start = System.nanoTime();
				block1 = works.get(worksPointer++);
				long stop = System.nanoTime();
				block2 = works.get(worksPointer++);

				//System.out.println("getTimes : "+(stop - start));

				totalGet += (stop - start);
				if((block2.right - block1.left) < 10){
					merge(block1.left,block1.right,block2.right,new Object[block2.right - block1.left + 1]);
				}else
					workers.add(Executors.callable(new MergeSortWorker(block1.left,block1.right,block2.right)));

				start = System.nanoTime();
				works.add(new SortBlock(block1.left,block2.right));
				stop = System.nanoTime();

				//System.out.println("addTimes : "+(stop - start));
				totalAdd += (stop - start);
				workSize = workSize-2;
				//��]�肪�o������ɉ�
				if(workSize == 1){
					SortBlock tmp = works.get(worksPointer++);
					works.add(tmp);
					break;
				}
			}


			try {
				executor.invokeAll(workers);
			} catch (InterruptedException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			}

			if(works.size() - tempSize == 2){  //�͈͂̏�񂪓�̂Ƃ��͍Ō�Ƀ}�[�W���ďI��
				block1 = works.get(worksPointer++);
				block2 = works.get(worksPointer);
				merge(block1.left,block1.right,block2.right,new Object[arrayLength]);
				break;
			}else if(works.size() - tempSize == 1)  //�͈͂̏�񂪈�̂Ƃ��͊������Ă���̂ŏI��
				break;
		}
	}




	@SuppressWarnings("unchecked")
	public synchronized void merge(int left,int mid,int right,Object[] buff){
		int i = left,j = mid + 1,k = 0;
		//Object[] buff = buff2;

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



	class MergeSortWorker implements Runnable{
		int left,right,mid;
		Object[] buff;
		public MergeSortWorker(int left,int right){
			this.left = left;
			this.right = right;
			mid = (left + right) / 2;
			buff = new Object[right - left + 1];
		}

		public MergeSortWorker(int left,int mid,int right){
			this.left = left;
			this.right = right;
			this.mid = mid;
			buff = new Object[right - left + 1];
		}
		public void run(){
			merge(left,mid,right,buff);
		}
	}


	class SortBlock{
		private int left,right;
		SortBlock(int left,int right){
			this.left = left;
			this.right = right;
		}

	}

	class QuickSortWorker implements Runnable {
		int left,right;
		public QuickSortWorker(int left,int right){
			this.left = left;
			this.right = right;
		}

		public void run() {
			quickSort(left,right);
		}
	}
}
