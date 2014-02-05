package upDownSort;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;



import tools.MyArrayUtil;
/*
 * volatile ���g���Ă���
 */


public class NoNameSort7<E extends Comparable> {
	E[] array;
	int arrayLength;
	//	volatile boolean[] isSorted;   //�\�[�g���������Ɩ����������̔���p
	//volatile boolean[] isSorted;   //�\�[�g���������Ɩ����������̔���p
	AtomicBoolean[] isSorted;   //�\�[�g���������Ɩ����������̔���p
	ExecutorService executor;
	LinkedList<Callable<Object>> workers;
	int[] posPointer;//����middle or right ���o���Ă����z��

	static final int ALLREVERSE = 1;
	static final int UPUP = 2;
	static final int UPDOWN = 3;
	static final int DOWNUP = 4;
	static final int DOWNDOWN = 6;
	static final int REST = 7;

	public NoNameSort7(){

	}

	public void sort(E[] array){
		this.array = array;
		arrayLength = array.length;
		isSorted = new AtomicBoolean[arrayLength];
		posPointer = new int[arrayLength];

		Arrays.fill(posPointer, -1);  //posPointer�̏�����

		//executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		for(int i = 0; i < arrayLength;i++){
			isSorted[i] = new AtomicBoolean();
		}
		executor = Executors.newFixedThreadPool(8);
		workers = new LinkedList<Callable<Object>>();

		//�ŏ���4�p�^�[���ɕ�����Ă���z����}�[�W���Ă����A���ׂĈ�p�^�[���̔z��ɂ���
		makingWorks();

		try {
			printAll();
			executor.invokeAll(workers);
			executor.shutdown();
			printAll();
		} catch (InterruptedException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

	}



	@SuppressWarnings("unchecked")
	public void makingWorks(){
		int pattern,left,num,pos,mid;
		left = num = pos = mid = 0;
		while(pos <= arrayLength - 1){
			pattern = 1;
			left = pos;
			for(int i = 0;i < 2;i++){		//for���ŉ�
				if(array[pos].compareTo(array[pos+1]) <= 0){	//�����Ȃ�
					pattern = pattern * (i + 1);
					//�ǂ��܂ŏ�ɏオ�邩
					while(array[pos].compareTo(array[pos+1]) <= 0){
						if(pos+1 >= arrayLength-1){
							if(i == 0){	//for������T�ڂȂ�
//								updateIsSorted(num,left,pos+1);//ox������
//								updatePosPointer(pos+1, left);
//								workers.add(Executors.callable(new MergeSortWorker(left,pos+1,num++,REST)));
								settingWork(left,pos+1,num,REST);
								return;
							}else{
								pos++;
								break;
							}
						}
						pos++;  //���ɐi�߂�
					}
				}else if(array[pos].compareTo(array[pos+1]) > 0){//�~���Ȃ�
					pattern = pattern * (i + 2);
					//�ǂ��܂ŉ��ɉ����邩
					while(array[pos].compareTo(array[pos+1]) > 0){

						//����i��0�@���@�Ō�܂ōs���Ă��܂�����MergeWorker�Ɏd���𓊂���return
						if(pos+1 >= arrayLength-1){
							if(i == 0){	//for������T�ڂȂ�
								//								updateIsSorted(0,left,pos+1);//ox������
								//								updatePosPointer(pos+1, left);
								//								workers.add(Executors.callable(new MergeSortWorker(left,pos+1,num++,ALLREVERSE)));
								settingWork(left,pos+1,num,ALLREVERSE);
								return;
							}else{
								pos++;
								break;
							}

						}
						pos++;
					}
				}

				if(i == 0){  //�܂���T�ڂȂ�l�X�ȏ���������
					mid = pos;
					pos++; //���ɐi�߂�

					if(pos == arrayLength - 1){
						insertSort(left,pos);
						//						updateIsSorted(num,left,pos);//ox������
						//						updatePosPointer(pos, left);
						//						workers.add(Executors.callable(new MergeSortWorker(left,pos,num++,REST)));
						settingWork(left,pos,num,REST);
						return;
					}
				}
			}//for���I��

			//MergeSortWorker�Ɏd���𓊂���
			updatePosPointer(left, pos);
			markBooleanFirst(left,mid,pos);
			workers.add(Executors.callable(new MergeSortWorker(left,mid,pos,num++,pattern)));
			pos++;  //���ɐi�߂�

			if(pos == arrayLength-1){
				posPointer[pos] = pos;//���̃|�W�V�����̕ۑ�
				isSorted[pos].set(true);
				workers.add(Executors.callable(new MergeSortWorker(pos,pos,num++,REST)));
				return;
			}
		}//whlie���I��
	}

	private void settingWork(int pos1,int pos2,int num,int pattern){
		if(pattern == ALLREVERSE)  //�]�肪���ׂč~���ł�������
			updateIsSorted(0,pos1,pos2);//ox������
		else
			updateIsSorted(num,pos1,pos2);//ox������

		updatePosPointer(pos1, pos2);
		workers.add(Executors.callable(new MergeSortWorker(pos1,pos2,num++,pattern)));

	}

	//posPointer���X�V
	private void updatePosPointer(int pos1, int pos2) {
		posPointer[pos1] = pos2;//���̃|�W�V�����̕ۑ�
		posPointer[pos2] = pos1; //�O�̃|�W�V�������ۑ�
	}


	public void insertSort(int left,int right){
		int i,j;
		E temp;
		for(i = left + 1; i <=right;i++) {
			temp = array[i];
			j = i;
			while(j > left && temp.compareTo(array[j-1])< 0){
				array[j] = array[j-1];
				j--;
			}
			array[j] = temp;
		}
	}




	class MergeSortWorker implements Runnable{
		int num,nextRight,id;
		int left,right,mid,pattern;

		public MergeSortWorker(int left,int mid,int right,int num,int pattern){
			this.num = num;
			this.pattern = pattern;
			this.left = left;
			this.mid = mid;
			this.right = right;
			id = num;

		}


		public MergeSortWorker(int left,int right,int num,int pattern){
			this.num = num;
			this.left = left;
			this.right = right;
			this.pattern = pattern;
			id = num;
		}


		public void run(){

			printAll("FIRST");


			switch(pattern){  //�ŏ��̃}�[�W�����͗l�X�Ȍ`������̂�switch���ŏ���
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

			updateIsSorted(num,left,right);

			//���ڂ̏������I���Ύ����珇���\�[�g���Ă���
			do{
				//num����������ŕ�����
				//�����ł����
				if(num % 2 == 0){
					//�]��ł���΁@�@�@����right��arrayLength - 1�Ŕ��f����
					if(right == arrayLength - 1){
						System.out.println("REST NUM : "+num);
						//�������Ď���������
						while(isSorted[left - 1].get() != isSorted[left].get()){
							System.out.println("["+id+"] is wating");
							printAll();

						}


						//����o�ɂȂ�����
						//�\�[�g�͈͂��g�債merge����
						mid = left - 1;
						left = posPointer[mid];

						merge(left,mid,right);
						printAll("REST");
						if(left == 0 && right == arrayLength - 1)
							return; //�Ō�̃\�[�g�ł�������I��


						//posPointer�̍X�V
						updatePosPointer(left,right);
						num = num / 2;	//num = num / 2;

						updateIsSorted(num,left,right);	//boolean���X�V
					}else{//�]��łȂ����
						//�E�̃\�[�g���I����Ă����
						if(isSorted[right].get() != isSorted[right + 1].get()){

							//�E��x�ɂȂ�����
							//�\�[�g�͈͂��g�債merge����
							mid = right;
							right = posPointer[mid+1];
							merge(left,mid,right);
							printAll("GUUSUU");
							if(left == 0 && right == arrayLength - 1)
								return; //�Ō�̃\�[�g�ł�������I��

							//posPointer�̍X�V
							updatePosPointer(left,right);
							//							posPointer[right] = left;
							//							posPointer[left] = right;	//merge�I�������int[left] = right�ɐݒ�
							num = num / 2;	//num = num / 2;

							updateIsSorted(num,left,right);	//boolean���X�V

						}else {//�E�̃\�[�g���I����Ă��Ȃ���ΏI��
							return;//�I��
						}

					}
				}else{  //��ł����
					//���������Ă݂�
					if(isSorted[left - 1].get() != isSorted[left].get()){  //���̃\�[�g���I����Ă����
						//�\�[�g�͈͂��g�債merge����
						mid = left - 1;
						left = posPointer[mid];

						merge(left,mid,right);
						printAll("ODD");
						if(left == 0 && right == arrayLength - 1)
							return; //�Ō�̃\�[�g�ł�������I��

						//posPointer�̍X�V
						updatePosPointer(left,right);
						//						posPointer[right] = left;
						//						posPointer[left] = right;
						num  = (num - 1) / 2;

						updateIsSorted(num,left,right);	//boolean���X�V

					}else //���̃\�[�g���I����Ă��Ȃ���΂��̃^�X�N�͏I��
						return;
				}
			}while(true);
		}



		private synchronized void printAll() {
			System.out.println(+num+"    left : "+left+"  right : "+right+"num : "+num);
			MyArrayUtil.print(array);
			MyArrayUtil.print(posPointer);
			printBoolean();
			System.out.println();
		}

		private synchronized void printAll(String st) {
			System.out.println(num+ "      "+st);
			System.out.println(+num+"    left : "+left+"  mid : +"+mid+"  right : "+right+" id : "+id+"num : "+num);
			MyArrayUtil.print(array);
			MyArrayUtil.print(posPointer);
			printBoolean();
			System.out.println();
		}

	}



	private synchronized void updateIsSorted(int num,int left,int right) {
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

	private synchronized void markBooleanFirst(int left,int mid,int right) {
		for(int i = left;i <= mid;i++){
			isSorted[i].set(true);
		}
		for(int i = mid+1;i <= right;i++){
			isSorted[i].set(false);
		}
	}


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



	private void reverse(int left, int right) {
		int range = right - left + 1;
		Object[] tmp = new Object[range];

		for(int i = 0;i < range;i++)
			tmp[i] = array[right-i];

		for(int i = 0;i < range;i++){
			array[i + left] = (E) tmp[i];
		}

	}


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
	private synchronized void printAll() {
		//System.out.println(+num+"    left : "+left+"  right : "+right+" id : "+id+"num : "+num);
		MyArrayUtil.print(array);
		MyArrayUtil.print(posPointer);
		printBoolean();
		System.out.println();
	}
}
