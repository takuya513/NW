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

public class QuickMergeSort6_4<E extends Comparable> {
	private int[] posPointer;//����middle or right ���o���Ă����z��
	private boolean[] isSorted;   //�\�[�g���������Ɩ����������̔���p   //�\�[�g���������Ɩ����������̔���p
	private final int SMALL = 20;
	private int taskNum;

	ExecutorService executor;
	int threadsNum, arrayLength, sortSection, left, right;
	int restPivot; //�}�[�W����Ƃ��ɗ]���������̎d�؂�
	LinkedList<Callable<Object>> workers;

	TestTools tt;

	public QuickMergeSort6_4(){


	}

	public long sort(E[] array){

		init(array);
		E[] buff = array.clone();
		long start = System.nanoTime();
		quickMergeSort(array,buff);
		long end =System.nanoTime();

		return end - start;



	}

	private void init(E[] array) {
		arrayLength = array.length;

		int threadsNum;
		if(arrayLength <= 100000)
			threadsNum = 5;
		else
			threadsNum = Runtime.getRuntime().availableProcessors();
		sortSection = arrayLength / (threadsNum * 8);
		left = 0;  right = sortSection - 1;
		restPivot = -1;
		taskNum = 0;

		executor = Executors.newFixedThreadPool(threadsNum);
		workers = new LinkedList<Callable<Object>>();

		isSorted = new boolean[arrayLength];
		posPointer = new int[arrayLength];

		//Arrays.fill(posPointer, -1);  //posPointer�̏�����
	}

	private void quickMergeSort(E[] array, E[] buff){
		//�N�C�b�N�\�[�g������
		while(right < arrayLength-1){
			//�܂�isSorted,posPointer�̐ݒ�
			settingWork(array,buff,left,right);
			left = right + 1;
			right =  sortSection + right;
		}
		//�������]��͓��ʂɑ����������邾���ł悢
		settingWork(array, buff, left,arrayLength-1);

		invokeTasks();
	}

	private void invokeTasks() {
		try {
			executor.invokeAll(workers);  //workers�̎d�������s���A�I���܂őҋ@
			executor.shutdown();
		} catch (InterruptedException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	private void settingWork(E[] array, E[] buff, int left,int right){
		updatePosPointer(left, right);
		firstIsSorted(left,right);
		workers.add(Executors.callable(new QuickMergeSortWorker(array,buff,left,right,taskNum++)));

	}

	private  void updateIsSorted(int num,int left,int right) {


		if(num%2 == 0){	//num����������炷�ׂĂ�true����
			isSorted[left] = true;
			isSorted[right] = true;
		}else{
			isSorted[left] = false;
			isSorted[right] = false;
		}
	}

	private void firstIsSorted(int left,int right) {


		isSorted[left] = true;
		isSorted[right] = false;
	}

	//posPointer���X�V
	private void updatePosPointer(int pos1, int pos2) {
		posPointer[pos1] = pos2;//���̃|�W�V�����̕ۑ�
		posPointer[pos2] = pos1; //�O�̃|�W�V�������ۑ�
	}

	class QuickMergeSortWorker implements Runnable{
		E[] array;
		E[] buff;
		private int num;
		private int left,right,mid;

		public QuickMergeSortWorker(E[] array, E[] buff, int left,int right,int num){
			this.num = num;
			this.left = left;
			this.right = right;
			this.array = array;
			this.buff = buff;
		}


		public void run(){
			//�͂��߂ɂ��̋敪�̃N�C�b�N�\�[�g,���̌�isSorted�̍X�V
			Arrays.sort(array,left,right + 1);
			//quickSort(array,left,right);
			//Arrays.sort(array);
			updateIsSorted(num,left,right);
			//printAll("FIRST");
			expanseMerge(array,buff);  //�����\�[�g���Ă���
		}


		private void expanseMerge(E[] array, E[] buff) {
			do{

				//num����������ŕ�����
				//�����ł����
				if(num % 2 == 0){

					//�]��ł���΁@�@�@����right��arrayLength - 1�Ŕ��f����
					if(right == arrayLength - 1){

						updateIsSorted(1,left,right);	//IsSorted�̍X�V
						if(checkSideIsSorted(left,false)){  //���̃\�[�g���I����Ă����
							//�\�[�g�͈͂��g�債merge����
							mid = left - 1;
							left = posPointer[mid];
							merge(array,buff,left,mid,right);
							//printAll("REST");
						}else{
							return;//�I��
						}


					}else{//�]��łȂ����
						if(checkSideIsSorted(right,true)){	//�E�̃\�[�g���I����Ă����
							//�\�[�g�͈͂��g�債merge����
							mid = right;
							right = posPointer[mid+1];
							merge(array, buff, left,mid,right);
							//printAll("GUUU");
						}else{ //�E�̃\�[�g���I����Ă��Ȃ���ΏI��
							return;//�I��
						}
					}
				}else{  //��ł����
					if(checkSideIsSorted(left,false)){  //���̃\�[�g���I����Ă����
						//�\�[�g�͈͂��g�債merge����
						mid = left - 1;
						left = posPointer[mid];
						merge(array, buff, left,mid,right);
						//printAll("KISU");
					}else{ //���̃\�[�g���I����Ă��Ȃ���΂��̃^�X�N�͏I��
						return;
					}
				}

				if(left == 0 && right == arrayLength - 1){	//�Ō�̃\�[�g�ł�������I��
					return;
				}

				updateAll();	//isSorted,posPointer,num�̍X�V
			}while(true);
		}


		private void updateAll() {
			//num�̍X�V
			if(num % 2 == 0)
				num = num / 2;
			else
				num  = (num - 1) / 2;
			updatePosPointer(left,right);//posPointer�̍X�V
			updateIsSorted(num,left,right);	//IsSorted�̍X�V
		}



	}

	private synchronized boolean checkSideIsSorted(int num,boolean isOdd){
		if(isOdd){  //�E��������Ƃ�
			if(isSorted[num] != isSorted[num + 1]){	//�E�̃\�[�g���I����Ă����
				isSorted[num] = !isSorted[num]; //�����̂���
				return true;
			}
			return false;
		}else{	//����������Ƃ�
			if(isSorted[num - 1] != isSorted[num]){  //���̃\�[�g���I����Ă����
				isSorted[num] = !isSorted[num];
				return true;
			}
			return false;
		}

	}

	public void merge(E[] array, E[] buff, int left,int mid,int right){
		//int i = left,j = mid + 1,k = left;

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







	private synchronized void printBoolean() {
	for(int i = 0;i < arrayLength;i++){
		if(isSorted[i]){
			System.out.print(" o");
		}else{
			System.out.print(" x");
		}
	}
	System.out.println();
}




}
