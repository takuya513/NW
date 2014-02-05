package upDownSort;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;


import tools.MyArrayUtil;
/*
 * �������Ă��Ȃ��A�o�O����
 */

public class NoNameSort9_2<E extends Comparable> {
	private E[] array;
	private int arrayLength,taskNum;
	private int[] posPointer;//����middle or right ���o���Ă����z��
	private AtomicBoolean[] isSorted;   //�\�[�g���������Ɩ����������̔���p
	private ExecutorService executor;
	private LinkedList<Callable<Object>> workers;

	static final int ALLREVERSE = 1;  //�w��͈̔͂̔z�񂪂��ׂč~���̂Ƃ�
	static final int UPUP = 2;	//�w��͈̔͂̔z�񂪏����A�����̑g�����̂Ƃ�
	static final int UPDOWN = 3;	//�w��͈̔͂̔z�񂪏����A�~���̑g�����̂Ƃ�
	static final int DOWNUP = 4;	//�w��͈̔͂̔z�񂪍~���A�����̑g�����̂Ƃ�
	static final int DOWNDOWN = 6;	//�w��͈̔͂̔z�񂪍~���A�~���̑g�����̂Ƃ�
	static final int REST = 7;		////�w��͈̔͂̔z�񂪗]��ł��菸���̂Ƃ�

	public NoNameSort9_2(){

	}

	public void sort(E[] array){
		init(array);  //�����ݒ�
		//printAll();
		createTasks();	//�^�X�N�𐶐�����
		//printAll();
		invokeTasks();  //�^�X�N�̎��s
		//printAll();
	}

	private void init(E[] array) {
		this.array = array;
		arrayLength = array.length;
		isSorted = new AtomicBoolean[arrayLength];
		posPointer = new int[arrayLength];
		taskNum = 0;

		Arrays.fill(posPointer, -1);  //posPointer�̏�����

		for(int i = 0; i < arrayLength;i++){
			isSorted[i] = new AtomicBoolean();
		}
		//executor = Executors.newFixedThreadPool(8);
		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		workers = new LinkedList<Callable<Object>>();
	}


	private void invokeTasks() {
		try {
			executor.invokeAll(workers);
			executor.shutdown();
		} catch (InterruptedException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void createTasks(){
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
							settingTask(left,right+1,REST); //�d�����Z�b�g����
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
							settingTask(left,right+1,ALLREVERSE);  //�d�����Z�b�g����
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
					settingTask(left,mid,right+1,pattern);  //MergeSortWorker�Ɏd���𓊂���
					return;
				}
			}else{	//��̃Z�b�g���ł����Ƃ�
				settingTask(left,mid,right,pattern);  //MergeSortWorker�Ɏd���𓊂���
				if(right+1 == arrayLength-1){  //���̈�̗v�f���z��̏I���Ȃ�
					settingTask(right+1,right+1,REST);  //MergeSortWorker�Ɏd���𓊂���
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
	private void settingTask(int left,int mid,int right,int pattern){
		updatePosPointer(left, right);
		firstIsSorted(left,mid,right);
		workers.add(Executors.callable(new MergeSortWorker(left,mid,right,taskNum++,pattern)));

	}

	private void settingTask(int pos1,int pos2,int pattern){
		if(pattern == ALLREVERSE || pos1 == pos2)  //�]�肪���ׂč~���ł�������,�������͍Ō�̈�ł�������
			updateIsSorted(0,pos1,pos2);//o������
		else
			updateIsSorted(taskNum,pos1,pos2);//ox������

		updatePosPointer(pos1, pos2);
		workers.add(Executors.callable(new MergeSortWorker(pos1,pos2,taskNum++,pattern)));

	}


	private synchronized void updateIsSorted(int num,int left,int right) {

//		if(num%2 == 0){	//num����������炷�ׂĂ�true����
//			isSorted[left].set(true);
//			isSorted[right].set(true);
//		}else{
//			isSorted[left].set(false);
//			isSorted[right].set(false);
//		}
		if(num%2 == 0){	//num����������炷�ׂĂ�true����
			for(int i = left;i <= right;i++){
				isSorted[i].set(true);
			}
		}else{
			for(int i = left;i <= right;i++){
				isSorted[i].set(false);
			}
		}
	}

	private void firstIsSorted(int left,int mid,int right) {

//		isSorted[left].set(true);
//		isSorted[right].set(false);
		for(int i = 0;i <= mid;i++){
			isSorted[i].set(true);
		}
		for(int i = mid + 1;i <= right;i++){
			isSorted[i].set(false);
		}
	}

	//posPointer���X�V
	private synchronized void updatePosPointer(int pos1, int pos2) {
		posPointer[pos1] = pos2;//���̃|�W�V�����̕ۑ�
		posPointer[pos2] = pos1; //�O�̃|�W�V�������ۑ�
	}


	class MergeSortWorker implements Runnable{
		private int num;
		private int left,right,mid,pattern;

		public MergeSortWorker(int left,int mid,int right,int num,int pattern){
			this.num = num;
			this.pattern = pattern;
			this.left = left;
			this.mid = mid;
			this.right = right;
		}


		public MergeSortWorker(int left,int right,int num,int pattern){
			this.num = num;
			this.left = left;
			this.right = right;
			this.pattern = pattern;
		}

		public void run(){
			firstMerge();  //�ŏ��̗l�X�ȃp�^�[���̃}�[�W

			expanseMerge();  //�����\�[�g���Ă���
		}


		private void firstMerge() {
			switch(pattern){
			case UPUP:
				merge(left,mid,right);
				break;
			case UPDOWN:
				mergeUpDown(left,mid,right);
				break;
			case DOWNUP:
				mergeDownUp(left,mid,right);
				break;
			case DOWNDOWN:
				mergeDownDown(left,mid,right);
				break;
			case ALLREVERSE:
				reverse(left,right);
			case REST:
				break;
			default :
				break;
			}
			updateIsSorted(num,left,right);  //IsSorted�̍X�V
		}

		private void expanseMerge() {
			do{
				//num����������ŕ�����
				//�����ł����
				if(num % 2 == 0){
					//�Ō�̃Z�b�g�ł���Ȃ��
					if(right == arrayLength - 1){
						//�������Ď���������
						//while(isSorted[left - 1].get() != isSorted[left].get());

						//����o�ɂȂ�����\�[�g�͈͂��g�債merge����
//						mid = left - 1;
//						left = posPointer[mid];
//						merge(left,mid,right);
						updateIsSorted(1,left,right);	//IsSorted�̍X�V
						return;
					}else{//�]��łȂ����
						if(isSorted[right].get() != isSorted[right + 1].get()){	//�E�̃\�[�g���I����Ă����
							//�\�[�g�͈͂��g�債merge����
							mid = right;
							right = posPointer[mid+1];
							merge(left,mid,right);

						}else //�E�̃\�[�g���I����Ă��Ȃ���ΏI��
							return;//�I��
					}
				}else{  //��ł����
					if(isSorted[left - 1].get() != isSorted[left].get()){  //���̃\�[�g���I����Ă����
						//�\�[�g�͈͂��g�債merge����
						mid = left - 1;
						left = posPointer[mid];
						merge(left,mid,right);
					}else //���̃\�[�g���I����Ă��Ȃ���΂��̃^�X�N�͏I��
						return;
				}

				if(left == 0 && right == arrayLength - 1)	//�Ō�̃\�[�g�ł�������I��
					return;

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
					System.out.println(+num+"    left : "+left+"  mid : "+mid+"  right : "+right+"num : "+num);
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


	@SuppressWarnings("unchecked")
	public void merge(int left,int mid,int right){
		Object[] buff = new Object[right - left + 1];
		int i = left,j = mid + 1,k = 0;

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

	@SuppressWarnings("unchecked")
	private void reverse(int left, int right) {
		int range = right - left + 1;
		Object[] tmp = new Object[range];

		for(int i = 0;i < range;i++)
			tmp[i] = array[right-i];

		for(int i = 0;i < range;i++){
			array[i + left] = (E) tmp[i];
		}

	}

	@SuppressWarnings("unchecked")
	public void mergeUpDown(int left,int mid,int right){
		Object[] buff = new Object[right - left + 1];
		int i = left,j = right,k = 0;

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
		for(i = left;i <= right; i++)
			array[i] = (E) buff[i - left];
	}

	@SuppressWarnings("unchecked")
	public void mergeDownUp(int left,int mid,int right){
		Object[] buff = new Object[right - left + 1];
		int i = mid,j = mid+1,k = 0;

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
		for(i = left;i <= right; i++)
			array[i] = (E) buff[i - left];
	}

	@SuppressWarnings("unchecked")
	public void mergeDownDown(int left,int mid,int right){
		Object[] buff = new Object[right - left + 1];
		int i = mid,j = right,k = 0;

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
		for(i = left;i <= right; i++)
			array[i] = (E) buff[i - left];
	}

	//�e�X�g�p
		private synchronized void printBoolean() {
			for(int i = 0;i < arrayLength;i++){
				if(isSorted[i].get()){
					System.out.print(" o");
				}else{
					System.out.print(" x");
				}
			}
			System.out.println();
		}

		private synchronized void printAll() {
			//System.out.println(+num+"    left : "+left+"  right : "+right+" id : "+id+"num : "+num);
			MyArrayUtil.print(array);
			MyArrayUtil.print(posPointer);
			printBoolean();
			System.out.println();
		}
}
