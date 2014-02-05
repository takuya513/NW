package upDownSort;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import tools.MyArrayUtil;


public class NoNameSort2<E extends Comparable> {
	E[] array,ori;
	int threadsNum,arrayLength,pos = 0,first,mid;
	volatile boolean[] tmp,ori2;
	boolean use;
	ExecutorService executor;
	LinkedList<Callable<Object>> workers;
	private int finalBlock;

	static final int UPUP = 1;
	static final int UPDOWN = 2;
	static final int DOWNUP = 3;
	static final int DOWNDOWN = 4;

	public NoNameSort2(){

	}

	public void sort(E[] array){
		this.array = array;
		this.ori = Arrays.copyOf(array, array.length);
		arrayLength = array.length;
		tmp = new boolean[arrayLength];
		use = true;

		executor = Executors.newFixedThreadPool(4);
		workers = new LinkedList<Callable<Object>>();

		//���i�K�A�ŏ���4�p�^�[���ɕ�����Ă���z����}�[�W���Ă����A���ׂĈ�p�^�[���̔z��ɂ���
		first();
		//�����܂łł�����e�X�g
		try {
			System.out.println("LastBlock : "+finalBlock);
			printBoolean();
			MyArrayUtil.print(array);
			executor.invokeAll(workers);
			executor.shutdown();
			MyArrayUtil.print(array);
			printBoolean();
		} catch (InterruptedException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

		//���i�K�A�z����}�[�W�\�[�g���Ă���
		//second();
	}



	@SuppressWarnings("unchecked")
	public void first(){
		int num = 0;
		while(pos+1 < arrayLength){
			first = pos;
			if(array[pos].compareTo(array[pos+1]) <= 0){
				while(array[pos].compareTo(array[pos+1]) <= 0){
					pos++;
					//�Ђ������葱������
					if(pos+1 >= arrayLength){
						System.out.println("UPUPUP");
						makeLastBlock(); //�Ō�̃u���b�N
						return;
					}
				}
				mid = pos;
				pos++;

				//�������̗v�f���Ȃ�������
				if(pos == arrayLength-1){  //�����̃\�[�g�͍l����
					makeTmpLast();
					return;
				}

				//�ǂ����ɓ]�Ԃ��H
				if(array[pos].compareTo(array[pos+1]) <= 0){  //�p�^�[��1
					System.out.println("pattern1");
					while(array[pos].compareTo(array[pos+1]) <= 0){
						pos++;
						if(pos+1 >= arrayLength){
							System.out.println("pattern1 Last");
							finalBlock = first; //�Ō�̃u���b�N�̓��ʏ����p
							break;
						}
					}
					markBoolean();
					workers.add(Executors.callable(new MergeSortWorker(first,mid,pos,num++,UPUP)));
				}else{  //�p�^�[���Q
					System.out.println("pattern2");
					while(array[pos].compareTo(array[pos+1]) > 0){
						pos++;

						if(pos+1 >= arrayLength){
							System.out.println("pattern2 Last");
							finalBlock = first;  //�Ō�̃u���b�N�̓��ʏ����p
							break;
						}
					}
					markBoolean();
					workers.add(Executors.callable(new MergeSortWorker(first,mid,pos,num++,UPDOWN)));
				}

			}else{
				while(array[pos].compareTo(array[pos+1]) > 0){
					pos++;
					if(pos+1 >= arrayLength){
						System.out.println("ALL REVERSE");
						reverse(first,pos);  //���ׂĂ��t���̂Ƃ��������̂ŋt�ɂ��ďI��
						makeLastBlock();
						return;
					}
				}
				mid = pos;
				pos++;

				//�������̗v�f���Ȃ�������
				if(pos == arrayLength-1){
					makeTmpLast();
					return;
				}

				//�ǂ����ɓ]�Ԃ��H
				if(array[pos].compareTo(array[pos+1]) <= 0){  //�p�^�[��3
					System.out.println("pattern3");
					while(array[pos].compareTo(array[pos+1]) <= 0){
						pos++;
						if(pos+1 >= arrayLength){
							System.out.println("pattern3 Last");
							finalBlock = first;  //�Ō�̃u���b�N�̓��ʏ����p
							break;
						}
					}
					//					merge3(first,mid,pos);  //�p�^�[��3�p�̃}�[�W
					markBoolean();
					workers.add(Executors.callable(new MergeSortWorker(first,mid,pos,num++,DOWNUP)));
				}else{  //�p�^�[��4
					System.out.println("pattern4");
					while(array[pos].compareTo(array[pos+1]) > 0){
						pos++;
						if(pos+1 >= arrayLength){
							System.out.println("pattern4 Last");
							finalBlock = first;  //�Ō�̃u���b�N�̓��ʏ����p
							break;
						}
					}
					markBoolean();
					workers.add(Executors.callable(new MergeSortWorker(first,mid,pos,num++,DOWNDOWN)));
				}
			}


			pos++;
			if(pos == arrayLength-1){
				System.out.println("THE LAST pos : "+pos);
				tmp[pos] = use;
				finalBlock = pos;  //�Ō�̃u���b�N�̓��ʏ����p
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

	private void makeTmpLast() {
		//merge(first,pos,pos,new Object[pos - first + 2]) ;   //�����ł���
		insertSort(first,pos);
		System.out.println("FIRSTONN : first : "+first+"  pos : "+pos );
		System.out.println("pos - first + 2 : "+(pos - first + 2));

		for(int i = first;i <=pos;i++){
			tmp[i] = use;
		}

		finalBlock = first;  //�Ō�̃u���b�N�̓��ʏ����p
	}

	private void makeLastBlock() {
		for(int i = first;i <=pos;i++){
			tmp[i] = use;
		}
		finalBlock = first;  //�Ō�̃u���b�N�̓��ʏ����p
	}

	private void markBoolean() {
		for(int i = first;i <= mid;i++){
			tmp[i] = use;
		}
		use = !use;   //�t��boolean����
		for(int i = mid+1;i <= pos;i++){
			tmp[i] = use;
		}
		use = !use;   //�t��boolean����
	}

	class MergeSortWorker implements Runnable{
		int num,tmpCount,id;
		int left,right,mid,pattern;
		boolean isNext = true;
		Object[] buff;



		public MergeSortWorker(int left,int mid,int right,int num,int pattern){
			this.num = num;
			this.pattern = pattern;
			this.left = left;
			this.mid = mid;
			this.right = right;
			id = num;
			ori2 = Arrays.copyOf(tmp, tmp.length);
		}


		public void run(){
			printAll();

			switch(pattern){
			case UPUP:
				mergeUpUp(left,mid,right);
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


			markBoolean2();

			do{
				if(num%2 == 0){	//���̎d���̏���������
					int tmptmp = 0;
					while(tmp[right] == tmp[right + 1]){  //�Ď���������B���̕������Ⴄboolean�ɂȂ�������s
						//printAll();
						System.out.println("tmpNum : "+id+ " waiting"+"   finalBlock : "+finalBlock);
						System.out.println();
						System.out.println("after");
						printAll();

						System.out.println("before");
						MyArrayUtil.print(ori);
						printBoolean(ori2);




						if(right+1 == finalBlock && num == 0 && tmp[right] == tmp[right+1]){//�Ō�̏���

							for(int i = right + 1;i < arrayLength;i++){
								while(tmp[right] != tmp[i]){

									System.out.println("tmp[right] : "+tmp[right]+"  tmp[i] : "+tmp[i]+"   finalBlock : "+finalBlock);
									printAll();
									MyArrayUtil.print(ori);
									printBoolean(ori2);
								}
							}

							System.out.println("LAST--");

							System.out.println("LAST--");
							merge(left,right, arrayLength-1, new Object[arrayLength]);
							printAll();
							System.out.println("  num : "+num+" temNum : "+id+" is End");
							return;
						}else if(right+1 == finalBlock && num != 0){  //�܂��A�Ō�ł͂Ȃ�����Ԍ��̋敪�������Ƃ�
							for(int i = right + 1;i < arrayLength;i++){
								if(tmp[right] != tmp[i]){  //���ׂāZ����Ȃ�������
									while(tmp[right] == tmp[right+1]){  //right�̎���boolean���Z�ɂȂ�܂ő҂�
										if(tmp[right] != tmp[right+1]){  //�����ɂȂ����甲����
											merge(left,right, arrayLength-1, new Object[arrayLength - left + 1 ]);

											printAll();
											finalBlock = left;
											System.out.println();
											System.out.println();
											endMarkBoolean2();
											System.out.println("  num : "+num+" temNum : "+id+" is End");
											return;
										}
									}
								}
							}

							merge(left,right, arrayLength-1, new Object[arrayLength - left + 1 ]);
							printAll();
							finalBlock = left;
							System.out.println("next FinalBlock : "+finalBlock);
							markBoolean2();
							System.out.println("  num : "+num+" temNum : "+id+" is End");

							return;
						}else if(right+1 == finalBlock && num == 0 ){//���ʂɍŌ�Ao--ox--x�ɕ����ꂽ��
							System.out.println("LAST++");
							printAll();
							System.out.println("LAST++");
							merge(left,right, arrayLength-1, new Object[arrayLength]);
							System.out.println("  num : "+num+" temNum : "+id+" is End");
							return;
						}

					}

					//num = num/2;

					//�V����right�����߂�
					for(tmpCount = right+1;tmpCount < arrayLength - 1;tmpCount++){
						if(tmp[tmpCount] != tmp[tmpCount+1]){
							//System.out.println("tmpCount "+tmpCount+"");
							break;
						}
					}

					mid = right;
					right = tmpCount;
					printAll();
					merge(left,mid, right, new Object[right - left + 1]);

					if(left == 0 && right == arrayLength-1)  //�����Ō�̏����ł�������
						return;

					markBoolean2();

					//��������������
					if(num != 0){
						while(tmp[left] != tmp[left - 1]);
					}
					//�����̐��񂪊m�F���ꂽ��
					num = num/2;
					markBoolean2();

				}else{
					isNext = false;  //���̃X���b�h�̎d���͏I���
					System.out.println("  num : "+num+" temNum : "+id);
				}
			}while(isNext);
		}

		private synchronized void markBoolean2() {
			if(num%2 == 0){	//num����������炷�ׂĂ�true����
				for(int i = left;i <= right;i++){
					tmp[i] = true;
				}
			}else{
				for(int i = left;i <= right;i++){
					tmp[i] = false;
				}
			}
		}

		private synchronized void endMarkBoolean2() {
			if(num%2 == 0){	//num����������炷�ׂĂ�true����
				for(int i = left;i <= arrayLength-1;i++){
					tmp[i] = true;
				}
			}else{
				for(int i = left;i <= arrayLength-1;i++){
					tmp[i] = false;
				}
			}
		}

		private synchronized void printAll() {
			System.out.println("left : "+left+"  right : "+right+"  num : "+num+" temNum : "+id);
			MyArrayUtil.print(array);
			printBoolean();
		}

		private synchronized void printAfter() {
			System.out.println("After temNum : "+id+"  pattern : "+pattern);
			MyArrayUtil.print(array);
			printBoolean();
		}





	}

	public void merge(int left,int mid,int right,Object[] buff){
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
			if(tmp[i] == true){
				System.out.print(" o");
			}else{
				System.out.print(" x");
			}
		}
		System.out.println();
	}

	private synchronized void printBoolean(boolean tmp[]) {
		for(int i = 0;i < arrayLength;i++){
			if(tmp[i] == true){
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

	public void mergeUpUp(int left,int mid,int right){
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
