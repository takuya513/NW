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
 * timSort�ł�����x�~���A�����̏ꍇ������
 */
public class QuickMergeSort6_2<E extends Comparable> {
	private int[] posPointer;//����middle or right ���o���Ă����z��
	private boolean[] isSorted;   //�\�[�g���������Ɩ����������̔���p   //�\�[�g���������Ɩ����������̔���p
	private final int SMALL = 20;
	private int taskNum;

	ExecutorService executor;
	int threadsNum, arrayLength, sortSection, left, right;
	int restPivot; //�}�[�W����Ƃ��ɗ]���������̎d�؂�
	LinkedList<Callable<Object>> workers;


	public QuickMergeSort6_2(){




	}

	public long sort(E[] array){

		init(array,Runtime.getRuntime().availableProcessors()-1);
		long start = System.nanoTime();
		quickMergeSort(array);
		long end =System.nanoTime();

		return end - start;



	}

	private void init(E[] array,int threadsNum) {
		arrayLength = array.length;

		sortSection = arrayLength / (threadsNum * 2);
		left = 0;  right = sortSection - 1;
		restPivot = -1;
		taskNum = 0;

		executor = Executors.newFixedThreadPool(threadsNum);
		workers = new LinkedList<Callable<Object>>();

		isSorted = new boolean[arrayLength];
		posPointer = new int[arrayLength];

		Arrays.fill(posPointer, -1);  //posPointer�̏�����
	}

	private void quickMergeSort(E[] array){
		//�N�C�b�N�\�[�g������
		while(right < arrayLength-1){
			//�܂�isSorted,posPointer�̐ݒ�
			settingWork(array,left,right);
			left = right + 1;
			right =  sortSection + right;
		}
		//�������]��͓��ʂɑ����������邾���ł悢
		settingWork(array, left,arrayLength-1);

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

	private void settingWork(E[] array, int left,int right){
		updatePosPointer(left, right);
		firstIsSorted(left,right);
		workers.add(Executors.callable(new QuickMergeSortWorker(array,left,right,taskNum++)));

	}

	private  void updateIsSorted(int num,int left,int right) {
//		if(num%2 == 0){	//num����������炷�ׂĂ�true����
//			for(int i = left;i <= right;i++){
//				isSorted[i] = true;
//			}
//		}else{
//			for(int i = left;i <= right;i++){
//				isSorted[i] = false;
//			}
//		}

		if(num%2 == 0){	//num����������炷�ׂĂ�true����
			isSorted[left] = true;
			isSorted[right] = true;
		}else{
			isSorted[left] = false;
			isSorted[right] = false;
		}
	}

	private void firstIsSorted(int left,int right) {
//		for(int i = left;i <= mid;i++){
//			isSorted[i] = true;
//		}
//		for(int i = mid+1;i <= right;i++){
//			isSorted[i] = false;
//		}

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

		private int num;
		private int left,right,mid;

		public QuickMergeSortWorker(E[] array, int left,int right,int num){
			this.num = num;
			this.left = left;
			this.right = right;
			this.array = array;

		}


		public void run(){
			//�͂��߂ɂ��̋敪�̃N�C�b�N�\�[�g,���̌�isSorted�̍X�V
			Arrays.sort(array,left,right + 1);
			//quickSort(array,left,right);
			//Arrays.sort(array);
			updateIsSorted(num,left,right);
			//printAll("FIRST");
			expanseMerge(array);  //�����\�[�g���Ă���
		}


		private void expanseMerge(E[] array) {
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
							Arrays.sort(array,left,right+1);
							//merge(array,,left,mid,right);
							//printAll("REST");
						}else{
							return;//�I��
						}


					}else{//�]��łȂ����
						if(checkSideIsSorted(right,true)){	//�E�̃\�[�g���I����Ă����
							//�\�[�g�͈͂��g�債merge����
							mid = right;
							right = posPointer[mid+1];
							Arrays.sort(array,left,right+1);
							//merge(array, , left,mid,right);
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
						Arrays.sort(array,left,right+1);
						//merge(array, , left,mid,right);
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


		//�e�X�g�p
		private synchronized void printAll() {
			System.out.println(+num+"    left : "+left+"  right : "+right+"num : "+num);
			MyArrayUtil.print(array);
			MyArrayUtil.print(posPointer);
			printBoolean();
			System.out.println();
		}

		private synchronized void printAll(String st) {
			System.out.println(num+ "      "+st);
			System.out.println(+num+"    left : "+left+"  mid : +"+mid+"  right : "+right+"num : "+num);
			MyArrayUtil.print(array);
			MyArrayUtil.print(posPointer);
			printBoolean();
			System.out.println();
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
