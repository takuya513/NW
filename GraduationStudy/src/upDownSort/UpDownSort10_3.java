package upDownSort;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * �o�[�W����8_3_6�̃}�[�W�����ŏ��Ȃ�������insertSort
 */


import tools.MyArrayUtil;
import upDownSort.NoNameSort9_2.MergeSortWorker;
/*
 * �ő��A
 */
public class UpDownSort10_3<E extends Comparable> {
	private int arrayLength,taskNum;
	private int[] posPointer;//����middle or right ���o���Ă����z��
	private volatile boolean[] isSorted;   //�\�[�g���������Ɩ����������̔���p
	private ExecutorService executor;
	private LinkedList<Future<Object>> futures;


	int time = 0; //�e�X�g

	static final int ALLREVERSE = 1;  //�w��͈̔͂̔z�񂪂��ׂč~���̂Ƃ�
	static final int UPUP = 2;	//�w��͈̔͂̔z�񂪏����A�����̑g�����̂Ƃ�
	static final int UPDOWN = 3;	//�w��͈̔͂̔z�񂪏����A�~���̑g�����̂Ƃ�
	static final int DOWNUP = 4;	//�w��͈̔͂̔z�񂪍~���A�����̑g�����̂Ƃ�
	static final int DOWNDOWN = 6;	//�w��͈̔͂̔z�񂪍~���A�~���̑g�����̂Ƃ�
	static final int REST = 7;		////�w��͈̔͂̔z�񂪗]��ł��菸���̂Ƃ�

	public UpDownSort10_3(){

	}

	public long sort(E[] array){

		time = 0;
		E[] buff = array.clone();
		long start = System.nanoTime();
		init(array);  //�����ݒ�
		createTasks(array,buff);	//�^�X�N�𐶐�����



		for(Future f:futures){
			try {
				f.get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			}
		}
		long end = System.nanoTime();

		executor.shutdown();
		return end - start;
	}

	private void init(Object[] array) {
		arrayLength = array.length;
		isSorted = new boolean[arrayLength];
		posPointer = new int[arrayLength];
		taskNum = 0;


		Arrays.fill(posPointer, -1);  //posPointer�̏�����

		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()-1);
		futures = new LinkedList<Future<Object>>();
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

				while(array[right].compareTo(array[right+1]) <= 0){  //�ǂ��܂ŏ��������ׂ�

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
		futures.add(executor.submit(Executors.callable(new MergeSortWorker(array, buff, left,mid,right,pattern))));
	}

	private void settingTask(E[] array,E[] buff, int left,int right,int pattern){
		updatePosPointer(left, right);
		futures.add(executor.submit(Executors.callable(new MergeSortWorker(array, buff, left,right,pattern))));
	}



	class MergeSortWorker implements Runnable{
		E[] array;
		E[] buff;
		private int left,right,mid,pattern;

		public MergeSortWorker(E[] array,E[] buff,int left,int mid,int right,int pattern){
			this.pattern = pattern;
			this.left = left;
			this.mid = mid;
			this.right = right;
			this.array = array;
			this.buff = buff;
		}


		public MergeSortWorker(E[] array,E[] buff,int left,int right,int pattern){
			this.left = left;
			this.right = right;
			this.pattern = pattern;
			this.array = array;
			this.buff = buff;
		}


		public void run(){

			firstMerge();  //�ŏ��̗l�X�ȃp�^�[���̃}�[�W

			if(left == 0 && right == arrayLength - 1)
				return;

			expanseMerge();  //�����\�[�g���Ă���

		}

		private void expanseMerge() {
			do{

				if(right == arrayLength - 1){

					if(checkLeftSideIsSorted(left,right)){  //���̃\�[�g���I����Ă����
						//�\�[�g�͈͂��g�債merge����
						mid = left - 1;
						left = posPointer[mid];
						merge(array,buff,left,mid,right);
					}else{
						return;//�I��
					}


				}else if(left == 0){//�ŏ��̗v�f�Ȃ�E��������
					if(checkRightSideIsSorted(right,left)){	//�E�̃\�[�g���I����Ă����
						//�\�[�g�͈͂��g�債merge����
						mid = right;
						right = posPointer[mid+1];
						merge(array,buff,left,mid,right);
					}else{ //�E�̃\�[�g���I����Ă��Ȃ���ΏI��
						return;//�I��
					}
				}else{    //�ŏ��̗v�f�ł��Ō�̗v�f�ł��Ȃ���
					if(checkLeftSideIsSorted(left,right)){  //���̃\�[�g���I����Ă����
						//�\�[�g�͈͂��g�債merge����
						mid = left - 1;
						left = posPointer[mid];
						merge(array,buff,left,mid,right);
					}else if(checkRightSideIsSorted(right,left)){	//�E�̃\�[�g���I����Ă����
						//�\�[�g�͈͂��g�債merge����
						mid = right;
						right = posPointer[mid+1];
						merge(array,buff,left,mid,right);
					}else{ //�ǂ���̃\�[�g���I����Ă��Ȃ����
						return;
					}
				}

				if(left == 0 && right == arrayLength - 1){	//�Ō�̃\�[�g�ł�������I��
					return;
				}

				updatePosPointer(left,right);//posPointer�̍X�V
				changeIsSorted(left,right,true);
			}while(true);
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
				break;
			default :
				break;

			}

			changeIsSorted(left,right,true);
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

			System.arraycopy(buff, left, array, left, right-left+1);
		}

		public void mergeDownUp(E[] array, E[] buff, int left,int mid,int right){

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

			System.arraycopy(buff, left, array, left, right-left+1);
		}

		public void mergeDownDown(E[] array, E[] buff, int left,int mid,int right){

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

			System.arraycopy(buff, left, array, left, right-left+1);
		}
	}


	//posPointer���X�V
	private void updatePosPointer(int pos1, int pos2) {
			posPointer[pos1] = pos2;//���̃|�W�V�����̕ۑ�
			posPointer[pos2] = pos1; //�O�̃|�W�V�������ۑ�
	}

	private  void changeIsSorted(int left,int right,boolean bool){
		synchronized(isSorted){
			isSorted[left] = bool;
			isSorted[right] = bool;
		}
	}



	private  boolean checkRightSideIsSorted(int right,int left){
		boolean bool = false;
		synchronized(isSorted){
			if((left != posPointer[right]) || (right != posPointer[left])){  //�����̗̈悪���łɃ}�[�W����Ă��邩�̊m�F
				return false;
			}

			if(isSorted[right] && isSorted[right+1]){	//�E�̃\�[�g���I����Ă����
				isSorted[left] = false;
				isSorted[right] = false;
				isSorted[posPointer[right+1]] = false;
				isSorted[right+1] = false;
				bool = true;
			}

		}
		return bool;
	}

	private  boolean checkLeftSideIsSorted(int left,int right){
		boolean bool = false;
		synchronized(isSorted){
			if((left != posPointer[right]) || (right != posPointer[left])){  //�����̗̈悪���łɃ}�[�W����Ă��邩�̊m�F
				return false;
			}

			if(isSorted[left] && isSorted[left-1]){	//���̃\�[�g���I����Ă����

				isSorted[left] = false;
				isSorted[right] = false;
				isSorted[posPointer[left-1]] = false;
				isSorted[left-1] = false;
				bool = true;
			}
		}
		return bool;
	}

	public void swap(E[] array,int i,int j){
		E temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}


}
