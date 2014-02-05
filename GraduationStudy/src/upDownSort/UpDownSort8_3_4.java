package upDownSort;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * ���S�ȓ��������߂���
 */


import tools.MyArrayUtil;
import upDownSort.NoNameSort9_2.MergeSortWorker;

public class UpDownSort8_3_4<E extends Comparable> {
	//private E[] array;
	private int arrayLength,taskNum;
	//private int[] posPointer;//����middle or right ���o���Ă����z��
	//private AtomicBoolean[] isSorted;   //�\�[�g���������Ɩ����������̔���p
	//private boolean[] isSorted;   //�\�[�g���������Ɩ����������̔���p
	private ExecutorService executor;
	private LinkedList<Callable<Object>> workers;

	static final int ALLREVERSE = 1;  //�w��͈̔͂̔z�񂪂��ׂč~���̂Ƃ�
	static final int UPUP = 2;	//�w��͈̔͂̔z�񂪏����A�����̑g�����̂Ƃ�
	static final int UPDOWN = 3;	//�w��͈̔͂̔z�񂪏����A�~���̑g�����̂Ƃ�
	static final int DOWNUP = 4;	//�w��͈̔͂̔z�񂪍~���A�����̑g�����̂Ƃ�
	static final int DOWNDOWN = 6;	//�w��͈̔͂̔z�񂪍~���A�~���̑g�����̂Ƃ�
	static final int REST = 7;		////�w��͈̔͂̔z�񂪗]��ł��菸���̂Ƃ�

	public UpDownSort8_3_4(){

	}

	public long sort(E[] array){
		init(array);  //�����ݒ�
		E[] buff = array.clone();
		int[] posPointer = new int[array.length];
		boolean[] isSorted = new boolean[array.length];

		long start = System.nanoTime();
		//long start2 = System.nanoTime();

		createTasks(array,buff,posPointer,isSorted);	//�^�X�N�𐶐�����
		//long end2 = System.nanoTime();
		startWorks();  //�^�X�N�̎��s
		long end = System.nanoTime();

		//System.out.println("FIEST  : "+(end2 - start2));
		//System.out.println("SECON  : "+(end - start));
		return end - start;
	}

	private void init(E[] array) {
		//this.array = array;
		arrayLength = array.length;
		taskNum = 0;



		//executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		executor = Executors.newFixedThreadPool(1);
		workers = new LinkedList<Callable<Object>>();
	}

	private void startWorks() {
		try {
			executor.invokeAll(workers);
			executor.shutdown();
		} catch (InterruptedException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}



	@SuppressWarnings("unchecked")
	public void createTasks(E[] array, E[] buff, int[] posPointer, boolean[] isSorted){

		int unitCount = 0;
		int pattern = 1;	//���������A�~���~���Ȃǂ̋�؂����z��̃}�[�W�p�^�[���̎w��p
		int left = 0;
		int mid = 0;
		int right = 0;

		while(right <= arrayLength - 1){

			if(array[right].compareTo(array[right+1]) <= 0){	//�����Ȃ�
				pattern = pattern * (unitCount + 1);
				while(array[right].compareTo(array[right+1]) <= 0){  //�ǂ��܂ŏ��������ׂ�
					if(right+1 >= arrayLength-1){	//�Ō�܂ŏ����ł����
						if(unitCount == 0){
							settingTask(array, buff, isSorted, posPointer, left,right+1,REST); //�d�����Z�b�g����
							return;
						}else{
							right++;	//���ɐi�߂�
							break;
						}
					}
					right++;  //���ɐi�߂�
				}

			}else {//�~���Ȃ�
				pattern = pattern * (unitCount + 2);
				while(array[right].compareTo(array[right+1]) > 0){//�ǂ��܂ō~�������ׂ�
					if(right+1 >= arrayLength-1){	//�Ō�܂ō~���ł����
						if(unitCount == 0){		//��̃Z�b�g�łȂ����
							settingTask(array,buff, isSorted, posPointer, left,right+1,ALLREVERSE);  //�d�����Z�b�g����
							return;
						}else{	//��̃Z�b�g�ł����
							right++;	//���ɐi�߂�
							break;
						}
					}
					right++;	//���ɐi�߂�
				}
			}

			if(unitCount == 0){  //��ڂ̏���or�~���𒲂ׂ��Ƃ�
				mid = right;
				unitCount++;
				if(right+1 == arrayLength - 1){	//���̈�̗v�f���z��̏I���Ȃ�
					if(pattern == 1) //pattern�Ŕ��肷��
						pattern = UPDOWN;
					else
						pattern = DOWNUP;
					settingTask(array,buff, isSorted, posPointer, left,mid,right+1,pattern);  //MergeSortWorker�Ɏd���𓊂���
					return;
				}
			}else{	//��̃Z�b�g���ł����Ƃ�
				settingTask(array, buff, isSorted, posPointer, left,mid,right,pattern);  //MergeSortWorker�Ɏd���𓊂���
				if(right+1 == arrayLength-1){  //���̈�̗v�f���z��̏I���Ȃ�
					settingTask(array, buff, isSorted, posPointer, right+1,right+1,REST);  //MergeSortWorker�Ɏd���𓊂���
					return;
				}
				pattern = 1; //������
				unitCount = 0;	//������
				left = right+1;
			}
			right++;  //���ɐi�߂�

		}//whlie���I��
	}

	//��Z�b�g�Ŏd��������ꍇ
	private void settingTask(E[] array, E[] buff,boolean[] isSorted,int[] posPointer,int left,int mid,int right,int pattern){
		updatePosPointer(posPointer, left, right);
		firstIsSorted(isSorted, left,mid,right);
		workers.add(Executors.callable(new MergeSortWorker(array, buff, isSorted,posPointer,left,mid,right,taskNum++,pattern)));

	}

	private void settingTask(E[] array,E[] buff,boolean[] isSorted,int[] posPointer, int pos1,int pos2,int pattern){
		if(pattern == ALLREVERSE)  //�]�肪���ׂč~���ł�������,�������͍Ō�̈�ł�������
			updateIsSorted(isSorted, 0,pos1,pos2);//o������
		else
			updateIsSorted(isSorted, taskNum,pos1,pos2);//ox������
		//updateIsSorted(0,pos1,pos2);//o������
		updatePosPointer(posPointer, pos1, pos2);
		workers.add(Executors.callable(new MergeSortWorker(array, buff, isSorted,posPointer,pos1,pos2,taskNum++,pattern)));

	}


	private synchronized void updateIsSorted(boolean[] isSorted,int num,int left,int right) {

		if(num%2 == 0){	//num����������炷�ׂĂ�true����
			isSorted[left] = true;
			isSorted[right] = true;
		}else{
			isSorted[left] = false;
			isSorted[right] = false;
		}
	}

	private void firstIsSorted(boolean[] isSorted,int left,int mid,int right) {

		isSorted[left] = true;
		isSorted[right] = false;
	}

	//posPointer���X�V
	private void updatePosPointer(int[] posPointer,int pos1, int pos2) {
		posPointer[pos1] = pos2;//���̃|�W�V�����̕ۑ�
		posPointer[pos2] = pos1; //�O�̃|�W�V�������ۑ�
	}




	class MergeSortWorker implements Runnable{
		E[] array;
		E[] buff;
		boolean[] isSorted;
		int[] posPointer;
		private int num,id;
		private int left,right,mid,pattern;

		public MergeSortWorker(E[] array,E[] buff,boolean[] isSorted, int[] posPointer, int left,int mid,int right,int num,int pattern){
			this.num = num;
			this.pattern = pattern;
			this.left = left;
			this.mid = mid;
			this.right = right;
			this.array = array;
			this.buff = buff;
			this.isSorted = isSorted;
			this.posPointer = posPointer;
		}


		public MergeSortWorker(E[] array,E[] buff,boolean[] isSorted, int[] posPointer, int left,int right,int num,int pattern){
			this.num = num;
			this.left = left;
			this.right = right;
			this.pattern = pattern;
			this.array = array;
			this.buff = buff;
			this.isSorted = isSorted;
			this.posPointer = posPointer;
		}


		public void run(){

			//long start = System.nanoTime();
			firstMerge();  //�ŏ��̗l�X�ȃp�^�[���̃}�[�W
			//long end = System.nanoTime();
			//System.out.println("first  : "+(end - start));

			//long start2 = System.nanoTime();
			expanseMerge();  //�����\�[�g���Ă���
			//long end2 = System.nanoTime();
			//System.out.println("SSSS"+num+"  : "+(end2 - start2));

		}


		private void expanseMerge() {
			do{

				//num����������ŕ�����
				//�����ł����
				if(num % 2 == 0){

					//�]��ł���΁@�@�@����right��arrayLength - 1�Ŕ��f����
					if(right == arrayLength - 1){

						updateIsSorted(isSorted, 1,left,right);	//IsSorted�̍X�V
						if(checkSideIsSorted(isSorted, left,false)){  //���̃\�[�g���I����Ă����
							//�\�[�g�͈͂��g�債merge����
							mid = left - 1;
							left = posPointer[mid];
							merge(array,buff,left,mid,right);
							//printAll("REST");
						}else{
							return;//�I��
						}


					}else{//�]��łȂ����
						if(checkSideIsSorted(isSorted, right,true)){	//�E�̃\�[�g���I����Ă����
							//�\�[�g�͈͂��g�債merge����
							mid = right;
							right = posPointer[mid+1];
							merge(array,buff,left,mid,right);
							//printAll("GUUU");
						}else{ //�E�̃\�[�g���I����Ă��Ȃ���ΏI��
							return;//�I��
						}
					}
				}else{  //��ł����
					if(checkSideIsSorted(isSorted, left,false)){  //���̃\�[�g���I����Ă����
						//�\�[�g�͈͂��g�債merge����
						mid = left - 1;
						left = posPointer[mid];
						merge(array,buff,left,mid,right);
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
			updatePosPointer(posPointer, left,right);//posPointer�̍X�V
			updateIsSorted(isSorted, num,left,right);	//IsSorted�̍X�V
		}

		//posPointer���X�V
		private void updatePosPointer(int[] posPointer,int pos1, int pos2) {
			posPointer[pos1] = pos2;//���̃|�W�V�����̕ۑ�
			posPointer[pos2] = pos1; //�O�̃|�W�V�������ۑ�
		}

		private void firstMerge() {
			switch(pattern){
			case UPUP:
				merge(array,buff,left,mid,right);
				break;
			case UPDOWN:
				mergeUpDown(array,buff,left,mid,right);
				break;
			case DOWNUP:
				mergeDownUp(array,buff,left,mid,right);
				break;
			case DOWNDOWN:
				mergeDownDown(array,buff,left,mid,right);
				break;
			case ALLREVERSE:
				reverse(array,buff,left,right);
				break;
			case REST:
				return;
			default :
				break;

			}

			updateIsSorted(isSorted, num,left,right);  //IsSorted�̍X�V
		}



//		private synchronized void printAll() {
//			System.out.println(+num+"    left : "+left+"  right : "+right+"num : "+num);
//			MyArrayUtil.print(array);
//			MyArrayUtil.print(posPointer);
//			printBoolean();
//			System.out.println();
//		}
//
//		private synchronized void printAll(String st) {
//			System.out.println("[["+id+"]]"+"      "+st);
//			System.out.println(+num+"    left : "+left+"  mid : +"+mid+"  right : "+right+" id : "+id+"num : "+num);
//			MyArrayUtil.print(array);
//			MyArrayUtil.print(posPointer);
//			printBoolean();
//			System.out.println();
//		}

	}

	private synchronized boolean checkSideIsSorted(boolean[] isSorted,int num,boolean isOdd){
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




	private void reverse(E[] array, E[] buff, int left, int right) {

		for(int i = left,j = 0;i <= right;i++,j++)
			buff[i] = array[right-j];

		System.arraycopy(buff, left, array, left, right-left+1);

	}


	public void mergeUpDown(E[] array, E[] buff, int left,int mid,int right){
		int i = left,j = right,k = left;

		while(i <= mid && j > mid) {
			if(array[i].compareTo(array[j]) < 0)
				buff[k++] = array[i++];
			else
				buff[k++] = array[j--];
		}

		while(i <= mid)
			buff[k++] = array[i++];
		while(j > mid)
			buff[k++] = array[j--];

		System.arraycopy(buff, left, array, left, right-left+1);
	}

	public void mergeDownUp(E[] array, E[] buff, int left,int mid,int right){
		int i = mid,j = mid+1,k = left;

		while(i >= left && j <= right) {
			if(array[i].compareTo(array[j]) < 0)
				buff[k++] = array[i--];
			else
				buff[k++] = array[j++];
		}

		while(i >= left)
			buff[k++] = array[i--];
		while(j <= right)
			buff[k++] = array[j++];

		System.arraycopy(buff, left, array, left, right-left+1);
	}

	public void mergeDownDown(E[] array, E[] buff, int left,int mid,int right){
		int i = mid,j = right,k = left;

		while(i >= left && j > mid) {
			if(array[i].compareTo(array[j]) < 0)
				buff[k++] = array[i--];
			else
				buff[k++] = array[j--];
		}

		while(i >= left)
			buff[k++] = array[i--];
		while(j > mid)
			buff[k++] = array[j--];

		System.arraycopy(buff, left, array, left, right-left+1);
	}




}
