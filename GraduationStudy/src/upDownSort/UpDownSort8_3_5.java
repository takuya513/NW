package upDownSort;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * �o�[�W����8_3_3�̃_�E���L���X�g�h�~�\�[�gand buff�̏C��
 */


import tools.MyArrayUtil;
import upDownSort.NoNameSort9_2.MergeSortWorker;
/*
 * �ő��A�����ɂ�array,buff�͏グ�Ƃ����`
 */
public class UpDownSort8_3_5<E extends Comparable> {
	//private E[] array;
	//private E[] buff;
	private int arrayLength,taskNum;
	private int[] posPointer;//����middle or right ���o���Ă����z��
	//private AtomicBoolean[] isSorted;   //�\�[�g���������Ɩ����������̔���p
	private boolean[] isSorted;   //�\�[�g���������Ɩ����������̔���p
	private ExecutorService executor;
	private LinkedList<Callable<Object>> workers;

	static final int ALLREVERSE = 1;  //�w��͈̔͂̔z�񂪂��ׂč~���̂Ƃ�
	static final int UPUP = 2;	//�w��͈̔͂̔z�񂪏����A�����̑g�����̂Ƃ�
	static final int UPDOWN = 3;	//�w��͈̔͂̔z�񂪏����A�~���̑g�����̂Ƃ�
	static final int DOWNUP = 4;	//�w��͈̔͂̔z�񂪍~���A�����̑g�����̂Ƃ�
	static final int DOWNDOWN = 6;	//�w��͈̔͂̔z�񂪍~���A�~���̑g�����̂Ƃ�
	static final int REST = 7;		////�w��͈̔͂̔z�񂪗]��ł��菸���̂Ƃ�

	public UpDownSort8_3_5(){

	}

	public long sort(E[] array2){

		Object[] array = array2;
		Object[] buff = new Object[array.length];
		long start = System.nanoTime();
		init(array);  //�����ݒ�
		//long start2 = System.nanoTime();

		createTasks(array,buff);	//�^�X�N�𐶐�����
		//long end2 = System.nanoTime();
		startWorks();  //�^�X�N�̎��s
		long end = System.nanoTime();

		//System.out.println("FIEST  : "+(end2 - start2));
		return end - start;
	}

	private void init(Object[] array) {
		//this.array = array;
		//this.buff = array.clone();
		arrayLength = array.length;
		isSorted = new boolean[arrayLength];
		posPointer = new int[arrayLength];
		taskNum = 0;


		Arrays.fill(posPointer, -1);  //posPointer�̏�����

		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
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
	public void createTasks(Object[] array, Object[] buff){

		int unitCount = 0;
		int pattern = 1;	//���������A�~���~���Ȃǂ̋�؂����z��̃}�[�W�p�^�[���̎w��p
		int left = 0;
		int mid = 0;
		int right = 0;

		while(right <= arrayLength - 1){

			if(((Comparable) array[right]).compareTo(array[right+1]) <= 0){	//�����Ȃ�
				pattern = pattern * (unitCount + 1);
				while(((Comparable) array[right]).compareTo(array[right+1]) <= 0){  //�ǂ��܂ŏ��������ׂ�
					if(right+1 >= arrayLength-1){	//�Ō�܂ŏ����ł����
						if(unitCount == 0){
							settingTask(array, buff, left,right+1,REST); //�d�����Z�b�g����
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
				while(((Comparable) array[right]).compareTo(array[right+1]) > 0){//�ǂ��܂ō~�������ׂ�
					if(right+1 >= arrayLength-1){	//�Ō�܂ō~���ł����
						if(unitCount == 0){		//��̃Z�b�g�łȂ����
							settingTask(array,buff, left,right+1,ALLREVERSE);  //�d�����Z�b�g����
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
					settingTask(array,buff, left,mid,right+1,pattern);  //MergeSortWorker�Ɏd���𓊂���
					return;
				}
			}else{	//��̃Z�b�g���ł����Ƃ�
				settingTask(array, buff, left,mid,right,pattern);  //MergeSortWorker�Ɏd���𓊂���
				if(right+1 == arrayLength-1){  //���̈�̗v�f���z��̏I���Ȃ�
					settingTask(array, buff, right+1,right+1,REST);  //MergeSortWorker�Ɏd���𓊂���
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
	private void settingTask(Object[] array,Object[] buff, int left,int mid,int right,int pattern){
		updatePosPointer(left, right);
		firstIsSorted(left,mid,right);
		workers.add(Executors.callable(new MergeSortWorker(array, buff, left,mid,right,taskNum++,pattern)));

	}

	private void settingTask(Object[] array,Object[] buff, int pos1,int pos2,int pattern){
		if(pattern == ALLREVERSE)  //�]�肪���ׂč~���ł�������,�������͍Ō�̈�ł�������
			updateIsSorted(0,pos1,pos2);//o������
		else
			updateIsSorted(taskNum,pos1,pos2);//ox������
		//updateIsSorted(0,pos1,pos2);//o������
		updatePosPointer(pos1, pos2);
		workers.add(Executors.callable(new MergeSortWorker(array, buff, pos1,pos2,taskNum++,pattern)));

	}


	private synchronized void updateIsSorted(int num,int left,int right) {

		if(num%2 == 0){	//num����������炷�ׂĂ�true����
			isSorted[left] = true;
			isSorted[right] = true;
		}else{
			isSorted[left] = false;
			isSorted[right] = false;
		}
	}

	private void firstIsSorted(int left,int mid,int right) {

		isSorted[left] = true;
		isSorted[right] = false;
	}

	//posPointer���X�V
	private void updatePosPointer(int pos1, int pos2) {
		posPointer[pos1] = pos2;//���̃|�W�V�����̕ۑ�
		posPointer[pos2] = pos1; //�O�̃|�W�V�������ۑ�
	}




	class MergeSortWorker implements Runnable{
		Object[] array;
		Object[] buff;
		private int num,id;
		private int left,right,mid,pattern;

		public MergeSortWorker(Object[] array2,Object[] buff2,int left,int mid,int right,int num,int pattern){
			this.num = num;
			this.pattern = pattern;
			this.left = left;
			this.mid = mid;
			this.right = right;
			this.array = array2;
			this.buff = buff2;
		}


		public MergeSortWorker(Object[] array,Object[] buff,int left,int right,int num,int pattern){
			this.num = num;
			this.left = left;
			this.right = right;
			this.pattern = pattern;
			this.array = array;
			this.buff = buff;
		}


		public void run(){

			firstMerge();  //�ŏ��̗l�X�ȃp�^�[���̃}�[�W
			expanseMerge();  //�����\�[�g���Ă���

		}


		private void expanseMerge() {
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
							merge(array,buff,left,mid,right);
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
			updatePosPointer(left,right);//posPointer�̍X�V
			updateIsSorted(num,left,right);	//IsSorted�̍X�V
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

			updateIsSorted(num,left,right);  //IsSorted�̍X�V
		}



		private synchronized void printAll() {
			System.out.println(+num+"    left : "+left+"  right : "+right+"num : "+num);
			MyArrayUtil.print(array);
			MyArrayUtil.print(posPointer);
			printBoolean();
			System.out.println();
		}

		private synchronized void printAll(String st) {
			System.out.println("[["+id+"]]"+"      "+st);
			System.out.println(+num+"    left : "+left+"  mid : +"+mid+"  right : "+right+" id : "+id+"num : "+num);
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

	public void merge(Object[] array, Object[] buff, int left,int mid,int right){
		//int i = left,j = mid + 1,k = left;

	     if (((Comparable) array[mid]).compareTo(array[mid+1]) <= 0) {
             return;
         }
//
//		while(i <= mid && j <= right) {
//			if(array[i].compareTo(array[j]) < 0)
//				buff[k++] = array[i++];
//			else
//				buff[k++] = array[j++];
//		}
//
//		while(i <= mid)
//			buff[k++] = array[i++];
//		while(j <= right)
//			buff[k++] = array[j++];
//		for(i = left;i <= right; i++)
//			array[i] = buff[i];

		for (int i = left, p = left, q = mid+1; i <= right; i++) {
			if (q > right || p <= mid && ((Comparable) array[p]).compareTo(array[q]) <= 0) {
				buff[i] = array[p++];
			} else {
				buff[i] = array[q++];
			}
		}
//
//		for(int i = left;i <= right; i++)
//			array[i] = buff[i];

		System.arraycopy(buff, left, array, left, right-left+1);

	}




	private void reverse(Object[] array, Object[] buff, int left, int right) {

		for(int i = left,j = 0;i <= right;i++,j++)
			buff[i] = array[right-j];

//		for(int i = left;i <= right;i++){
//			array[i] = buff[i];
//		}

		System.arraycopy(buff, left, array, left, right-left+1);

	}


	public void mergeUpDown(Object[] array, Object[] buff, int left,int mid,int right){
		int i = left,j = right,k = left;

		while(i <= mid && j > mid) {
			if(((Comparable) array[i]).compareTo(array[j]) < 0)
				buff[k++] = array[i++];
			else
				buff[k++] = array[j--];
		}

		while(i <= mid)
			buff[k++] = array[i++];
		while(j > mid)
			buff[k++] = array[j--];
//		for(i = left;i <= right; i++)
//			array[i] = buff[i];

		System.arraycopy(buff, left, array, left, right-left+1);
	}

	public void mergeDownUp(Object[] array, Object[] buff, int left,int mid,int right){
		int i = mid,j = mid+1,k = left;

		while(i >= left && j <= right) {
			if(((Comparable) array[i]).compareTo(array[j]) < 0)
				buff[k++] = array[i--];
			else
				buff[k++] = array[j++];
		}

		while(i >= left)
			buff[k++] = array[i--];
		while(j <= right)
			buff[k++] = array[j++];
//		for(i = left;i <= right; i++)
//			array[i] = buff[i];

		System.arraycopy(buff, left, array, left, right-left+1);
	}

	public void mergeDownDown(Object[] array, Object[] buff, int left,int mid,int right){
		int i = mid,j = right,k = left;

		while(i >= left && j > mid) {
			if(((Comparable) array[i]).compareTo(array[j]) < 0)
				buff[k++] = array[i--];
			else
				buff[k++] = array[j--];
		}

		while(i >= left)
			buff[k++] = array[i--];
		while(j > mid)
			buff[k++] = array[j--];
//		for(i = left;i <= right; i++)
//			array[i] = buff[i];

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
