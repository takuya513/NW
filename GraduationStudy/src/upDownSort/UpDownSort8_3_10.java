package upDownSort;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * �o�[�W����8_3_6�̃}�[�W�����ŏ��Ȃ�������insertSort
 */


import tools.MyArrayUtil;
import upDownSort.NoNameSort9_2.MergeSortWorker;
/*
 * �ő��A�����ɂ�array,buff�͏グ�Ƃ����`,binarySearch���ċA���g�킸�\��
 */
public class UpDownSort8_3_10<E extends Comparable> {
	//private E[] array;
	//private E[] buff;
	private int arrayLength,taskNum;
	private int[] posPointer;//����middle or right ���o���Ă����z��
	//private AtomicBoolean[] isSorted;   //�\�[�g���������Ɩ����������̔���p
	private boolean[] isSorted;   //�\�[�g���������Ɩ����������̔���p
	private ExecutorService executor;
	private LinkedList<Callable<Object>> workers;

	int time = 0; //�e�X�g

	static final int ALLREVERSE = 1;  //�w��͈̔͂̔z�񂪂��ׂč~���̂Ƃ�
	static final int UPUP = 2;	//�w��͈̔͂̔z�񂪏����A�����̑g�����̂Ƃ�
	static final int UPDOWN = 3;	//�w��͈̔͂̔z�񂪏����A�~���̑g�����̂Ƃ�
	static final int DOWNUP = 4;	//�w��͈̔͂̔z�񂪍~���A�����̑g�����̂Ƃ�
	static final int DOWNDOWN = 6;	//�w��͈̔͂̔z�񂪍~���A�~���̑g�����̂Ƃ�
	static final int REST = 7;		////�w��͈̔͂̔z�񂪗]��ł��菸���̂Ƃ�

	public UpDownSort8_3_10(){

	}

	public long sort(E[] array){

		time = 0;
		E[] buff = array.clone();
		long start = System.nanoTime();
		init(array);  //�����ݒ�
		//long start2 = System.nanoTime();

		createTasks(array,buff);	//�^�X�N�𐶐�����
		//long end2 = System.nanoTime();
		startWorks();  //�^�X�N�̎��s
		long end = System.nanoTime();

		//System.out.println("FIEST  : "+(end2 - start2));
		//System.out.println("setTime: "+time);
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

		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()-1);
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
	public void createTasks(E[] array, E[] buff){

		int unitCount = 0;
		int pattern = 1;	//���������A�~���~���Ȃǂ̋�؂����z��̃}�[�W�p�^�[���̎w��p
		int left = 0;
		int mid = 0;
		int right = 0;


		while(right <= arrayLength - 1){


			if( array[right].compareTo(array[right+1]) <= 0){	//�����Ȃ�

				pattern = pattern * (unitCount + 1);

				//long st = System.nanoTime();
				while(array[right].compareTo(array[right+1]) <= 0){  //�ǂ��܂ŏ��������ׂ�

					if(right+1 >= arrayLength-1){	//�Ō�܂ŏ����ł����
						//long ed = System.nanoTime();
						//time += ed - st;
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
				while(array[right].compareTo(array[right+1]) > 0){//�ǂ��܂ō~�������ׂ�
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
	private void settingTask(E[] array,E[] buff, int left,int mid,int right,int pattern){
		updatePosPointer(left, right);
		firstIsSorted(left,mid,right);
		workers.add(Executors.callable(new MergeSortWorker(array, buff, left,mid,right,taskNum++,pattern)));

	}

	private void settingTask(E[] array,E[] buff, int pos1,int pos2,int pattern){
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
		E[] array;
		E[] buff;
		private int num,id;
		private int left,right,mid,pattern;

		public MergeSortWorker(E[] array2,E[] buff2,int left,int mid,int right,int num,int pattern){
			this.num = num;
			this.pattern = pattern;
			this.left = left;
			this.mid = mid;
			this.right = right;
			this.array = array2;
			this.buff = buff2;
		}


		public MergeSortWorker(E[] array,E[] buff,int left,int right,int num,int pattern){
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

				//System.out.println("left : "+left+"  right : "+right);
				//MyArrayUtil.print(array);
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

		@SuppressWarnings("unchecked")
		public void merge(E[] array, E[] buff, int left,int mid,int right){
			if (array[mid].compareTo(array[mid+1]) <= 0) {
				return;
			}


			if(right -  left < 7){
				for(int i = left;i <= right;i++){
					for(int j = i; j > left && array[j - 1].compareTo(array[j]) > 0;j--)
						swap(array,j,j-1);
				}
				return;
			}

			int i = left,j = mid + 1,k = i;
			int add = 1;
			int lastPos = i;
			int endPos = mid;


			//���ŒT�����Ă���
			while(i <= mid && j <= right){

				if(array[i].compareTo(array[j]) < 0){

					lastPos = i;
					while(array[i].compareTo(array[j]) < 0){
						add *=2;
						i+=add;
						endPos = i;
						if(i > mid){
							endPos = mid;
							break;
						}
					}
					i-=add;

					if(lastPos != i){  //�|�W�V����(i)�̈ʒu���ω�������
						System.arraycopy(array,lastPos,buff,k,i - lastPos);
						k+=i - lastPos;

						lastPos = i;
					}



					if(add >= 4){
						i = binarySearch(array,lastPos,endPos,array[j],endPos);
						System.arraycopy(array,lastPos,buff,k,i-lastPos);
						k+=i-lastPos;
					}else{
						while(array[i].compareTo(array[j]) < 0 && i <= endPos){
							buff[k++] = array[i++];
						}
					}

					buff[k++] = array[j++];
					add = 1;

				}else{
					buff[k++] = array[j++];
				}

			}

			if(i <= mid){
				System.arraycopy(array,i, buff, k, mid-i+1);
			}
			if(j <= right){
				System.arraycopy(array,j, buff, k, right - j +1);
			}
			System.arraycopy(buff, left, array, left, right-left+1);


		}


		@SuppressWarnings("unchecked")
		public int binarySearch(E[] array,int left1,int right1,E key,int end){

			int left = left1;
			int right = right1;

			while(true){
				if(right - left == 0){
					if(array[left].compareTo(key) <= 0)
						return left+1;
					return left;
				}

				if(left > right)
					return left;

				int mid = (left + right) / 2;

				if(array[mid].compareTo(key) < 0){
					//return binarySearch(array,mid+1,right,key,end);
					left = mid+1;
				}else if(array[mid].compareTo(key) == 0){
					//return binarySearch(array,left+1,right,key,end);
					left = left+1;
				}else{
					//return binarySearch(array,left,mid-1,key,end);
					right = mid-1;
				}
			}
		}


		private void reverse(E[] array, E[] buff, int left, int right) {

			for(int i = left,j = right;i <= right;i++,j--)
				buff[i] = array[j];


			System.arraycopy(buff, left, array, left, right-left+1);

		}


		public void mergeUpDown(E[] array, E[] buff, int left,int mid,int right){

			//int length = right-  left+1;



			if(right-  left < 7){
				for(int i = left;i <= right;i++){
					for(int j = i; j > left && array[j - 1].compareTo(array[j]) > 0;j--)
						swap(array,j,j-1);
				}
				return;
			}

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
			//			for(i = left;i <= right; i++)
			//				array[i] = buff[i];

			System.arraycopy(buff, left, array, left, right-left+1);
		}

		public void mergeDownUp(E[] array, E[] buff, int left,int mid,int right){
			//int length = right-  left+1;



			if(right-  left < 7){
				for(int i = left;i <= right;i++){
					for(int j = i; j > left && array[j - 1].compareTo(array[j]) > 0;j--)
						swap(array,j,j-1);
				}
				return;
			}


			int i = mid,j = mid+1,k = left;

			while(i >= left && j <= right) {
				if( array[i].compareTo(array[j]) < 0)
					buff[k++] = array[i--];
				else
					buff[k++] = array[j++];
			}

			while(i >= left)
				buff[k++] = array[i--];
			while(j <= right)
				buff[k++] = array[j++];
			//			for(i = left;i <= right; i++)
			//				array[i] = buff[i];

			System.arraycopy(buff, left, array, left, right-left+1);
		}

		public void mergeDownDown(E[] array, E[] buff, int left,int mid,int right){
			//int length = right -  left+1;

			if(right -  left < 7){
				for(int i = left;i <= right;i++){
					for(int j = i; j > left && array[j - 1].compareTo(array[j]) > 0;j--)
						swap(array,j,j-1);
				}
				return;
			}


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
			//			for(i = left;i <= right; i++)
			//				array[i] = buff[i];

			System.arraycopy(buff, left, array, left, right-left+1);
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

	public void swap(E[] array,int i,int j){
		E temp = array[i];
		array[i] = array[j];
		array[j] = temp;
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
