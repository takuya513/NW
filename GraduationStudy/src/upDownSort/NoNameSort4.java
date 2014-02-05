package upDownSort;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import tools.MyArrayUtil;


public class NoNameSort4<E extends Comparable> {
	E[] array;
	int threadsNum,arrayLength,pos = 0,first,mid;
	volatile boolean[] isSorted,ori2;
	ExecutorService executor;
	LinkedList<Callable<Object>> workers;
	int[] nextPos;//����middle or right ���o���Ă����z��

	static final int UPUP = 1;
	static final int UPDOWN = 2;
	static final int DOWNUP = 3;
	static final int DOWNDOWN = 4;

	public NoNameSort4(){

	}

	public void sort(E[] array){
		this.array = array;
		arrayLength = array.length;
		isSorted = new boolean[arrayLength];
		nextPos = new int[arrayLength];

		Arrays.fill(isSorted,true);
		executor = Executors.newFixedThreadPool(4);
		workers = new LinkedList<Callable<Object>>();

		//���i�K�A�ŏ���4�p�^�[���ɕ�����Ă���z����}�[�W���Ă����A���ׂĈ�p�^�[���̔z��ɂ���
		first();
		//�����܂łł�����e�X�g
		try {

			executor.invokeAll(workers);
			executor.shutdown();

		} catch (InterruptedException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

	}



	@SuppressWarnings("unchecked")
	public void first(){
		int num = 0;
		while(pos+1 < arrayLength){  //�z��̍Ō�܂�
			first = pos;
			if(array[pos].compareTo(array[pos+1]) <= 0){
				while(array[pos].compareTo(array[pos+1]) <= 0){
					pos++;
					//�Ђ������葱������
					if(pos+1 >= arrayLength){
						markBoolean(num,first,pos);//ox������

						nextPos[first] = pos;//���̃|�W�V�����̕ۑ�
						workers.add(Executors.callable(new MergeSortWorker(first,pos,num++)));
						return;
					}
				}
				mid = pos;
				pos++;

				//�������̗v�f���Ȃ�������
				if(pos == arrayLength-1){
					markBoolean(num,first,pos);//ox������
					nextPos[first] = pos;//���̃|�W�V�����̕ۑ�
					insertSort(first,pos);
					workers.add(Executors.callable(new MergeSortWorker(first,pos,num++)));
					return;
				}

				//�ǂ����ɓ]�Ԃ��H
				if(array[pos].compareTo(array[pos+1]) <= 0){  //�p�^�[��1
					while(array[pos].compareTo(array[pos+1]) <= 0){
						pos++;
						if(pos+1 >= arrayLength){
							break;
						}
					}
					markBooleanFirst(first,mid,pos);
					nextPos[first] = pos;//���̃|�W�V�����̕ۑ�
					workers.add(Executors.callable(new MergeSortWorker(first,mid,pos,num++,UPUP)));
				}else{  //�p�^�[���Q
					while(array[pos].compareTo(array[pos+1]) > 0){
						pos++;

						if(pos+1 >= arrayLength){
							break;
						}
					}
					markBooleanFirst(first,mid,pos);
					nextPos[first] = pos;//���̃|�W�V�����̕ۑ�
					workers.add(Executors.callable(new MergeSortWorker(first,mid,pos,num++,UPDOWN)));
				}

			}else{
				while(array[pos].compareTo(array[pos+1]) > 0){
					pos++;
					if(pos+1 >= arrayLength){
						reverse(first,pos);  //���ׂĂ��t���̂Ƃ��������̂ŋt�ɂ��ďI��
						nextPos[first] = pos;//���̃|�W�V�����̕ۑ�
						workers.add(Executors.callable(new MergeSortWorker(first,pos,num++)));
						markBoolean(num,first,pos);	//ox������
						return;
					}
				}
				mid = pos;
				pos++;   //���ɐi�߂�

				//�������̗v�f���Ȃ�������
				if(pos == arrayLength-1){  //�]��̔z�񂩂Ō�Ɉ�����c������
					markBoolean(num,first,pos);//ox������
					nextPos[first] = pos;//���̃|�W�V�����̕ۑ�
					insertSort(first,pos);
					workers.add(Executors.callable(new MergeSortWorker(first,pos,num++)));
					return;
				}

				//�ǂ����ɓ]�Ԃ��H
				if(array[pos].compareTo(array[pos+1]) <= 0){  //�p�^�[��3
					while(array[pos].compareTo(array[pos+1]) <= 0){
						//System.out.println("pattern3");
						pos++;
						if(pos+1 >= arrayLength){
							System.out.println("pattern3 Last");
							break;
						}
					}
					nextPos[first] = pos;//���̃|�W�V�����̕ۑ�
					markBooleanFirst(first,mid,pos);
					workers.add(Executors.callable(new MergeSortWorker(first,mid,pos,num++,DOWNUP)));
				}else{  //�p�^�[��4
					while(array[pos].compareTo(array[pos+1]) > 0){
						//System.out.println("pattern4");
						pos++;
						if(pos+1 >= arrayLength){
							//System.out.println("pattern4 Last");
							break;
						}
					}
					nextPos[first] = pos;//���̃|�W�V�����̕ۑ�
					markBooleanFirst(first,mid,pos);
					workers.add(Executors.callable(new MergeSortWorker(first,mid,pos,num++,DOWNDOWN)));

				}
			}


			pos++;
			if(pos == arrayLength-1){
				nextPos[pos] = pos;//���̃|�W�V�����̕ۑ�
				isSorted[pos] = true;
				workers.add(Executors.callable(new MergeSortWorker(pos,pos,num++)));
			}
		}


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

		public MergeSortWorker(int left,int mid,int right,int num,int pattern){
			this.num = num;
			this.pattern = pattern;
			this.left = left;
			this.mid = mid;
			this.right = right;
			//id = num;

		}


		public MergeSortWorker(int left,int right,int num){
			this.num = num;
			this.left = left;
			this.right = right;
			isRest = true;
			//id = num;
		}


		public void run(){


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

			}

			markBoolean(num,left,right);

			//���ڂ̏������I���Ύ����珇���\�[�g���Ă���
			do{
				//num����������ŕ�����
				//�����ł����
				if(num % 2 == 0){
					//�]��ł���΁@�@�@����right��arrayLength - 1�Ŕ��f����
					if(right == arrayLength - 1){
						//�������Ď���������
						while(isSorted[left - 1] != isSorted[left]);
						//����o�ɂȂ�����
						markBoolean(1,left,right);  //����boolean��x�ɂ���
						return;
					}else{//�]��łȂ����
						//�E���Ď���������
						while(isSorted[right] == isSorted[right + 1]);
						//�E��x�ɂȂ�����
						//�\�[�g�͈͂��g�債merge����
						mid = right;
						right = nextPos[mid+1];
						merge(left,mid,right);
						if(left == 0 && right == arrayLength - 1)
							return; //�Ō�̃\�[�g�ł�������I��


						nextPos[left] = right;	//merge�I�������int[left] = right�ɐݒ�
						num = num / 2;	//num = num / 2;

						markBoolean(num,left,right);	//boolean���X�V
					}
				}else {//��ł����
					return;//�I��
				}
			}while(true);



		}


		private synchronized void printAll() {
			//System.out.println(+num+"    left : "+left+"  right : "+right+" id : "+id+"num : "+num);
			MyArrayUtil.print(array);
			MyArrayUtil.print(nextPos);
			printBoolean();
			System.out.println();
		}


		private synchronized void printAll(String st) {
			System.out.println(num+ "      "+st);
			//System.out.println(+num+"    left : "+left+"  right : "+right+" id : "+id+"num : "+num);
			MyArrayUtil.print(array);
			MyArrayUtil.print(nextPos);
			printBoolean();
			System.out.println();
		}

	}

	private synchronized void markBoolean(int num,int left,int right) {
		if(num%2 == 0){	//num����������炷�ׂĂ�true����
			for(int i = left;i <= right;i++){
				isSorted[i] = true;
			}
		}else{
			for(int i = left;i <= right;i++){
				isSorted[i] = false;
			}
		}
	}

	private synchronized void markBooleanFirst(int left,int mid,int right) {
			for(int i = left;i <= mid;i++){
				isSorted[i] = true;
			}
			for(int i = mid+1;i <= right;i++){
				isSorted[i] = false;
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
			if(isSorted[i] == true){
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

}
