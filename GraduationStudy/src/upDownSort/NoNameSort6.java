package upDownSort;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;



import tools.MyArrayUtil;
/*
 * atomic �œ�����
 */

public class NoNameSort6<E extends Comparable> {
	E[] array;
	int threadsNum,arrayLength,pos = 0,first,mid;
	AtomicBoolean[] isSorted;   //�\�[�g���������Ɩ����������̔���p
	ExecutorService executor;
	LinkedList<Callable<Object>> workers;
	int[] nextPos;//����middle or right ���o���Ă����z��

	static final int UPUP = 2;
	static final int UPDOWN = 3;
	static final int DOWNUP = 4;
	static final int DOWNDOWN = 6;

	public NoNameSort6(){

	}

	public void sort(E[] array){
		this.array = array;
		arrayLength = array.length;
		isSorted = new AtomicBoolean[arrayLength];
		nextPos = new int[arrayLength];

//		Arrays.fill(isSorted,true);
		for(int i = 0; i < arrayLength;i++){
			isSorted[i] = new AtomicBoolean();
		}
		
		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		workers = new LinkedList<Callable<Object>>();

		//�ŏ���4�p�^�[���ɕ�����Ă���z����}�[�W���Ă����A���ׂĈ�p�^�[���̔z��ɂ���
		makingWork();

		try {

			executor.invokeAll(workers);
			executor.shutdown();
		} catch (InterruptedException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

	}


	@SuppressWarnings("unchecked")
	public void makingWork(){
		int pattern,left = 0,num = 0;
		//�傫���g�g��
		while(pos <= arrayLength - 1){
			pattern = 1;
			left = pos;
			for(int i = 0;i < 2;i++){		//for�œ���
				if(array[pos].compareTo(array[pos+1]) <= 0){	//�����Ȃ�
					pattern = pattern * (i + 1);
					//�ǂ��܂ŏ�ɏオ�邩
					while(array[pos].compareTo(array[pos+1]) <= 0){
						if(pos+1 >= arrayLength-1){
							if(i == 0){
								updateIsSorted(num,left,pos+1);//ox������
								nextPos[left] = pos+1;//���̃|�W�V�����̕ۑ�
//								workers.add(Executors.callable(new MergeSortWorker(left,pos+1,num++,false)));
								workers.add(Executors.callable(new MergeSortWorker(left,pos+1,num++)));
								return;
							}else{
								pos++;
								break;
							}
						}
						pos++;
					}
				}else if(array[pos].compareTo(array[pos+1]) > 0){//�~���Ȃ�
					pattern = pattern * (i + 2);
					//�ǂ��܂ŉ��ɉ����邩
					while(array[pos].compareTo(array[pos+1]) > 0){

						//����i��0�@���@�Ō�܂ōs���Ă��܂�����MergeWorker�Ɏd���𓊂���return
						if(pos+1 >= arrayLength-1){
							if(i == 0){
								reverse(left,pos+1);  //���ׂĂ��t���̂Ƃ��������̂ŋt�ɂ��ďI��
								System.out.println("OBJECT");
								updateIsSorted(0,left,pos+1);//���ׂĂ��~���]��ł���΂��ׂāZ�ɂ��Ă���
								nextPos[left] = pos+1;//���̃|�W�V�����̕ۑ�
//								workers.add(Executors.callable(new MergeSortWorker(left,pos+1,num++,true)));
								workers.add(Executors.callable(new MergeSortWorker(left,pos+1,num++)));
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
						updateIsSorted(num,left,pos);//ox������
//						workers.add(Executors.callable(new MergeSortWorker(left,pos,num++,false)));
						workers.add(Executors.callable(new MergeSortWorker(left,pos,num++)));
						nextPos[left] = pos;//���̃|�W�V�����̕ۑ�
						return;
					}
				}
			}//for���I��

			//MergeSortWorker�Ɏd���𓊂���
			nextPos[left] = pos;//���̃|�W�V�����̕ۑ�
			markFirstIsSorted(left,mid,pos);
			workers.add(Executors.callable(new MergeSortWorker(left,mid,pos,num++,pattern)));
			pos++;  //���ɐi�߂�
			if(pos == arrayLength-1){
				nextPos[pos] = pos;//���̃|�W�V�����̕ۑ�
				isSorted[pos].set(true);
				//workers.add(Executors.callable(new MergeSortWorker(pos,pos,num++,false)));
				workers.add(Executors.callable(new MergeSortWorker(pos,pos,num++)));
				return;
			}
		}//whlie���I��
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
		int num,nextRight;//id;
		int left,right,mid,pattern;
		boolean isRest = false;
		boolean isReverse = false;  //���ׂĂ��t���̔z�񂩂̊m�F

		public MergeSortWorker(int left,int mid,int right,int num,int pattern){
			this.num = num;
			this.pattern = pattern;
			this.left = left;
			this.mid = mid;
			this.right = right;
			//id = num;

		}


//		public MergeSortWorker(int left,int right,int num,boolean isReverse){
//			this.num = num;
//			this.left = left;
//			this.right = right;
//			this.isReverse = isReverse;
//			isRest = true;
//			//id = num;
//		}
		
		public MergeSortWorker(int left,int right,int num){
			this.num = num;
			this.left = left;
			this.right = right;
			isRest = true;
			//id = num;
		}


		public void run(){
			//printAll();

			if(!isRest){   //�]��̔z�񂶂�Ȃ����,�͂��߂̃\�[�g���s��
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
				default :
					break;

				}
					
//			}else if(isReverse)  //�]�肩���ׂĂ��~���ł������珸���ɒ���
//				reverse(left,right);
			}
			updateIsSorted(num,left,right);

			//���ڂ̏������I���Ύ����珇���\�[�g���Ă���
			do{
				//num����������ŕ�����
				//�����ł����
				if(num % 2 == 0){
					//�]��ł���΁@�@�@����right��arrayLength - 1�Ŕ��f����
					if(right == arrayLength - 1){
						//�������Ď���������
						while(isSorted[left - 1].get() != isSorted[left].get());
						//����o�ɂȂ�����
						updateIsSorted(1,left,right);  //����boolean��x�ɂ���
						return;
					}else{//�]��łȂ����
						//�E���Ď���������
						while(isSorted[right].get() == isSorted[right + 1].get());
						//�E��x�ɂȂ�����
						//�\�[�g�͈͂��g�債merge����
						mid = right;
						right = nextPos[mid+1];
						merge(left,mid,right);
						if(left == 0 && right == arrayLength - 1)
							return; //�Ō�̃\�[�g�ł�������I��


						nextPos[left] = right;	//merge�I�������int[left] = right�ɐݒ�
						num = num / 2;	//num = num / 2;

						updateIsSorted(num,left,right);	//boolean���X�V
					}
				}else {//��ł����
					return;//�I��
				}
			}while(true);
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

	private synchronized void markFirstIsSorted(int left,int mid,int right) {
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
	//���ׂĂ̗v�f���~���������Ƃ��ɏ����ɒ������\�b�h
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
	
	
	private synchronized void printAll(String st) {
		//System.out.println(num+ "      "+st);
		//System.out.println(+num+"    left : "+left+"  right : "+right+" id : "+id+"num : "+num);
		MyArrayUtil.print(array);
		MyArrayUtil.print(nextPos);
		printBoolean();
		System.out.println();
	}
	

	private synchronized void printAll() {
		//System.out.println(+num+"    left : "+left+"  right : "+right+" id : "+id+"num : "+num);
		MyArrayUtil.print(array);
		MyArrayUtil.print(nextPos);
		printBoolean();
		System.out.println();
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

}
