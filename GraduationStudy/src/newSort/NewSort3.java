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
/*
 * �ŏ��̃N�C�b�N�\�[�g�̈ꕔ��ʂ̃X���b�h�ɔC���悤�Ƃ�������
 * �������A�Ȃ����o�O���łĂ��܂�
 */
public class NewSort3 <E extends Comparable> extends QuickSort<E>{
	ExecutorService executor;
	int threadsNum,arrayLength,pos,first;
	List<Callable<Object>> workers;
	ArrayList<SortBlock> works;
	ArrayList<SortBlock> works2;
	boolean endArray = false;
	boolean firstSet = false;
	int[] posInfo;
	public BlockingQueue<SortBlock> queue;
	TestTools tt;


	public NewSort3() {
		tt = new TestTools();
		threadsNum = Runtime.getRuntime().availableProcessors()-1;
		queue = new LinkedBlockingQueue<SortBlock>();
	}


	public void sort(E[] array){
		this.array = array;
		arrayLength = array.length;
		executor = Executors.newFixedThreadPool(threadsNum);
		workers = new ArrayList<Callable<Object>>(threadsNum);
		works = new ArrayList<SortBlock>();

		pos = 0;

		QuickSortWorker2 thread = new QuickSortWorker2(queue);
		thread.start();

		while(true){
			if(array[pos].compareTo(array[pos+1]) <= 0)
				ascendingOrder();
			else
				descendindOrder();

			pos++;
			if(endArray == true)
				break;
			else if(pos+1 > arrayLength-1){
				works.add(new SortBlock(pos,pos));
				break;
			}
		}

		thread.isKeepPut();
		//�C���A�K�X���s�ł���悤�ɂ���
		try {

			executor.invokeAll(workers);
			thread.join();

			//�}�[�W���郁�\�b�h
			parallelMergeSort();

			executor.invokeAll(workers);
		}catch (InterruptedException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		executor.shutdown();

//		System.out.println("testCount : "+testCount);
//		System.out.println("testCOunt2 : "+testCount2);

	}

	//�������\�b�h
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
			try {
				queue.put(new SortBlock(first,pos));
			} catch (InterruptedException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			}
		}else
			workers.add(Executors.callable(new QuickSortWorker(first,pos)));//�C���A�����ƌ����悭������

		works.add(new SortBlock(first,pos));
		return;
	}

	//�}�[�W���镔��
	public void parallelMergeSort(){
		int workSize;
		SortBlock block1,block2;
		while(true){
			workSize = works.size();

			while(workSize > 1){
				//���works�����o���Aleft,right�����o��
				block1 = works.remove(0);
				block2 = works.remove(0);

				if((block2.right - block1.left) < 10){
					merge(block1.left,block1.right,block2.right,new Object[block2.right - block1.left + 1]);
				}else
					workers.add(Executors.callable(new MergeSortWorker(block1.left,block1.right,block2.right)));

				works.add(new SortBlock(block1.left,block2.right));

				workSize = workSize-2;
				//��]�肪�o������ɉ�
				if(workSize == 1){
					SortBlock tmp = works.remove(0);
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

			if(works.size() == 2){  //�͈͂̏�񂪓�̂Ƃ��͍Ō�Ƀ}�[�W���ďI��
				block1 = works.remove(0);
				block2 = works.remove(0);
				merge(block1.left,block1.right,block2.right,new Object[arrayLength]);
				break;
			}else if(works.size() == 1)  //�͈͂̏�񂪈�̂Ƃ��͊������Ă���̂ŏI��
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

	class QuickSortWorker2 extends Thread{
		public BlockingQueue<SortBlock> queue;
		private boolean keepPut = true;   //�d���̔z���͂Â��Ă��邩
		SortBlock sortBlock;
		public QuickSortWorker2(BlockingQueue<SortBlock> queue){
			this.queue = queue;
		}

		public void isKeepPut(){
			keepPut = false;
		}

		public void run(){

			while(keepPut || ( queue.size() > 0)){

				//int a = 0;
				//System.out.println();
				try {

					sortBlock = queue.take();
					//tt.p("bok",sortBlock.left,"ddd",sortBlock.right);
					quickSort(sortBlock.left,sortBlock.right);
				} catch (InterruptedException e) {
					// TODO �����������ꂽ catch �u���b�N
					e.printStackTrace();
				}



			}
			//tt.p("finish");
			//tt.p("queue size",queue.size() );
		}

	}


//	class Channel {
//		private final BlokingList queue;
//
//		public Channel(){
//			queue = new ArrayList();
//		}
//
//		public void putRequest(QuickInfo info){
//			queue.add(info);
//		}
//	}
}
