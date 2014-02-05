package upDownSort;

import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import tools.MyArrayUtil;


public class NoNameSort<E extends Comparable> {
	E[] array;
	int threadsNum,arrayLength,pos = 0,first,mid;
	volatile boolean[] tmp;
	boolean use;
	ExecutorService executor;
	LinkedList<Callable<Object>> workers;
	private AtomicInteger blockNum;
	private int finalBlock;

	static final int UPUP = 1;
	static final int UPDOWN = 2;
	static final int DOWNUP = 3;
	static final int DOWNDOWN = 4;
	public NoNameSort(){

	}

	public void sort(E[] array){
		this.array = array;
		arrayLength = array.length;
		tmp = new boolean[arrayLength];
		use = true;
		blockNum = new AtomicInteger(0);

		executor = Executors.newFixedThreadPool(4);
		workers = new LinkedList<Callable<Object>>();

		//���i�K�A�ŏ���4�p�^�[���ɕ�����Ă���z����}�[�W���Ă����A���ׂĈ�p�^�[���̔z��ɂ���
		first();
		//�����܂łł�����e�X�g
		try {
			System.out.println("FIRST blockNum : "+blockNum.get());
			printTmp();
			MyArrayUtil.print(array);
			executor.invokeAll(workers);
			executor.shutdown();
			MyArrayUtil.print(array);
			printTmp();
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

					if(pos+1 >= arrayLength){
						System.out.println("UUUU");
						makeLastBlock(); //�Ō�̃u���b�N
						return;
					}
				}
				mid = pos;
				pos++;

				//�������̗v�f���Ȃ�������
				if(pos == arrayLength-1){
					makeTmpLast();
					break;
				}

				//�ǂ����ɓ]�Ԃ��H
				if(array[pos].compareTo(array[pos+1]) <= 0){  //�p�^�[��1
					System.out.println("pattern1");
					while(array[pos].compareTo(array[pos+1]) <= 0){
						pos++;
						if(pos+1 >= arrayLength){

							finalBlock = first;  //�Ō�̃u���b�N�̓��ʏ����p
							break;
						}
					}
					makeTmp();
					workers.add(Executors.callable(new MergeSortWorker(first,mid,pos,num++,UPUP)));
				}else{  //�p�^�[���Q
					System.out.println("pattern2");
					while(array[pos].compareTo(array[pos+1]) > 0){
						pos++;

						if(pos+1 >= arrayLength){

							finalBlock = first;  //�Ō�̃u���b�N�̓��ʏ����p
							break;
						}
					}
					makeTmp();
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
					break;
				}

				//�ǂ����ɓ]�Ԃ��H
				if(array[pos].compareTo(array[pos+1]) <= 0){  //�p�^�[��3
					System.out.println("pattern3");
					while(array[pos].compareTo(array[pos+1]) <= 0){
						pos++;
						if(pos+1 >= arrayLength){

							finalBlock = first;  //�Ō�̃u���b�N�̓��ʏ����p
							break;
						}
					}
					//					merge3(first,mid,pos);  //�p�^�[��3�p�̃}�[�W
					makeTmp();
					workers.add(Executors.callable(new MergeSortWorker(first,mid,pos,num++,DOWNUP)));
				}else{  //�p�^�[��4
					System.out.println("pattern4");
					while(array[pos].compareTo(array[pos+1]) > 0){
						pos++;
						if(pos+1 >= arrayLength){
							finalBlock = first;  //�Ō�̃u���b�N�̓��ʏ����p
							break;
						}
					}
					makeTmp();
					workers.add(Executors.callable(new MergeSortWorker(first,mid,pos,num++,DOWNDOWN)));
				}
			}


			pos++;
			if(pos == arrayLength-1){
				System.out.println("THE LAST pos : "+pos);
				blockNum.incrementAndGet();
				tmp[pos] = use;
				finalBlock = pos;  //�Ō�̃u���b�N�̓��ʏ����p
			}
		}


	}

	private void makeTmpLast() {
		merge(first,pos-1,pos,new Object[pos - first + 1]) ;   //�����ł���
		System.out.println("FIRSTONN");
		blockNum.addAndGet(1);


		for(int i = first;i <=pos;i++){
			tmp[i] = use;
		}

		finalBlock = first;  //�Ō�̃u���b�N�̓��ʏ����p
	}

	private void makeLastBlock() {
		blockNum.incrementAndGet();
		for(int i = first;i <=pos;i++){
			tmp[i] = use;
		}
	}

	private void makeTmp() {
		blockNum.addAndGet(2);
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
		int num,tmpCount;
		int left,right,mid,pattern;
		boolean isNext = true;
		Object[] buff;

		public MergeSortWorker(int left,int mid,int right,int num,int pattern){
			this.num = num;
			this.pattern = pattern;
			this.left = left;
			this.mid = mid;
			this.right = right;
		}

		public MergeSortWorker(int left,int right,int num){
			this.left = left;
			this.right = right;
			this.num = num;
		}

		public void run(){
			printAll();



			switch(pattern){
			case UPUP:
				merge1(left,mid,right);
				break;
			case UPDOWN:
				merge2(left,mid,right);
				break;
			case DOWNUP:
				merge3(left,mid,right);
				break;
			case DOWNDOWN:
				merge4(left,mid,right);
				break;
			default :
				break;

			}



			if(num%2 == 0){	//num����������炷�ׂĂ�true����
				for(int i = left;i <= right;i++){
					tmp[i] = true;
				}
			}else{
				for(int i = left;i <= right;i++){
					tmp[i] = false;
				}
			}
			blockNum.decrementAndGet();

			do{



				if(num%2 == 0){	//���̎d���̏���������


					while(tmp[right] == tmp[right + 1]){  //�Ď���������B���̕������Ⴄboolean�ɂȂ�������s

						System.out.println("num : "+num+ "waiting");
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO �����������ꂽ catch �u���b�N
							e.printStackTrace();
						}
						if(blockNum.get() == 2 && num == 0){//�Ō�̏���
							System.out.println("LAST--");
							printAll();
							System.out.println("LAST--");
							merge(left,right, arrayLength-1, new Object[arrayLength]);
							return;
						}
					}

					num = num/2;
					//�V����right�����߂�

					for(tmpCount = right+1;tmpCount < arrayLength - 1;tmpCount++){
						if(tmp[tmpCount] != tmp[tmpCount+1]){
							System.out.println("tmpCount "+tmpCount+"");
							break;
						}
					}

					mid = right;
					right = tmpCount;
					System.out.println("newLeft : "+left+"  newMid : "+mid+"  newRight : "+right);
					printAll();
					merge(left,mid, right, new Object[right - left + 1]);



					if(num%2 == 0){	//num����������炷�ׂĂ�true����
						for(int i = left;i <= right;i++){
							tmp[i] = true;
						}
					}else{
						for(int i = left;i <= right;i++){
							tmp[i] = false;
						}
					}

					blockNum.decrementAndGet();   //�u���b�N�̌���������
					printAll();
					System.out.println();
				}else{
					isNext = false;  //���̃X���b�h�̎d���͏I���
					System.out.println("num "+num+" is End");
				}
			}while(isNext);
		}

		private synchronized void printAll() {
			System.out.println("left : "+left+"  right : "+right+"  arrayLength : "+arrayLength+" num : "+num+"  pattern : "+pattern+"  blcokNum "+blockNum.get());
			MyArrayUtil.print(array);
			printTmp();
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

	private synchronized void printTmp() {
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

	public void merge1(int left,int mid,int right){
		Object[] buff = new Object[right - left + 1];
		int i = left,j = mid + 1,k = 0;

		System.out.println("ENTER");
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

	public void merge2(int left,int mid,int right){
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

	public void merge3(int left,int mid,int right){
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

	public void merge4(int left,int mid,int right){
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
