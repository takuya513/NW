package upDownSort;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import tools.MyArrayUtil;


public class NoNameSort3<E extends Comparable> {
	E[] array,ori;
	int threadsNum,arrayLength,pos = 0,first,mid;
	volatile boolean[] isSorted,ori2;
	boolean judge;
	ExecutorService executor;
	LinkedList<Callable<Object>> workers;
	private int finalBlock;

	static final int UPUP = 1;
	static final int UPDOWN = 2;
	static final int DOWNUP = 3;
	static final int DOWNDOWN = 4;

	public NoNameSort3(){

	}

	public void sort(E[] array){
		this.array = array;
		this.ori = Arrays.copyOf(array, array.length);
		arrayLength = array.length;
		isSorted = new boolean[arrayLength];
		judge = true;
		
		Arrays.fill(isSorted,true);
		executor = Executors.newFixedThreadPool(4);
		workers = new LinkedList<Callable<Object>>();

		//第一段階、最初に4パターンに分かれている配列をマージしていき、すべて一パターンの配列にする
		first();
		//ここまでできたらテスト
//		try {
//			System.out.println("LastBlock : "+finalBlock);
			printBoolean();
			MyArrayUtil.print(array);
//			ori2 = Arrays.copyOf(isSorted, isSorted.length);
//			executor.invokeAll(workers);
//			executor.shutdown();
//			MyArrayUtil.print(array);
//			printBoolean();
//			System.out.println("before");
//			MyArrayUtil.print(ori);
//			//printBoolean(ori2);
//		} catch (InterruptedException e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		}

	}



	@SuppressWarnings("unchecked")
	public void first(){
		int num = 0;
		while(pos+1 < arrayLength){  //配列の最後まで
			first = pos;
			if(array[pos].compareTo(array[pos+1]) <= 0){
				while(array[pos].compareTo(array[pos+1]) <= 0){
					pos++;
					//ひたすら上り続けた時
					if(pos+1 >= arrayLength){
						markBoolean(num,first,pos);//oxをつける
						workers.add(Executors.callable(new MergeSortWorker(first,pos,num++)));
						return;
					}
				}
				mid = pos;
				pos++;

				//もし次の要素がなかったら
				if(pos == arrayLength-1){
					//markBoolean(num,first,pos);//oxをつける
					insertSort(first,pos);
					workers.add(Executors.callable(new MergeSortWorker(first,pos,num++)));
					return;
				}

				//どっちに転ぶか？
				if(array[pos].compareTo(array[pos+1]) <= 0){  //パターン1
					while(array[pos].compareTo(array[pos+1]) <= 0){
						pos++;
						if(pos+1 >= arrayLength){
							finalBlock = first; //最後のブロックの特別処理用
							break;
						}
					}
					//markBoolean(num,first,pos);//oxをつける
					workers.add(Executors.callable(new MergeSortWorker(first,mid,pos,num++,UPUP)));
				}else{  //パターン２
					while(array[pos].compareTo(array[pos+1]) > 0){
						pos++;

						if(pos+1 >= arrayLength){
							finalBlock = first;  //最後のブロックの特別処理用
							break;
						}
					}
					//markBoolean(num,first,pos);	//oxをつける
					workers.add(Executors.callable(new MergeSortWorker(first,mid,pos,num++,UPDOWN)));
				}

			}else{
				while(array[pos].compareTo(array[pos+1]) > 0){
					pos++;
					if(pos+1 >= arrayLength){
						reverse(first,pos);  //すべてが逆順のときだったので逆にして終了
						workers.add(Executors.callable(new MergeSortWorker(first,pos,num++)));
						//markFinalBoolean();
						return;
					}
				}
				mid = pos;
				pos++;   //次に進める

				//もし次の要素がなかったら
				if(pos == arrayLength-1){  //余りの配列かつ最後に一つだけ残ったら
					//markBoolean(num,first,pos);//oxをつける
					insertSort(first,pos);
					workers.add(Executors.callable(new MergeSortWorker(first,pos,num++)));
					return;
				}

				//どっちに転ぶか？
				if(array[pos].compareTo(array[pos+1]) <= 0){  //パターン3
					while(array[pos].compareTo(array[pos+1]) <= 0){
						System.out.println("pattern3");
						pos++;
						if(pos+1 >= arrayLength){
							System.out.println("pattern3 Last");
							finalBlock = first;  //最後のブロックの特別処理用
							break;
						}
					}
					//markBoolean(num,first,pos);	//oxをつける
					workers.add(Executors.callable(new MergeSortWorker(first,mid,pos,num++,DOWNUP)));
				}else{  //パターン4
					while(array[pos].compareTo(array[pos+1]) > 0){
						System.out.println("pattern4");
						pos++;
						if(pos+1 >= arrayLength){
							System.out.println("pattern4 Last");
							finalBlock = first;  //最後のブロックの特別処理用
							break;
						}
					}
					//markBoolean(num,first,pos);
					workers.add(Executors.callable(new MergeSortWorker(first,mid,pos,num++,DOWNDOWN)));
					
				}
			}


			pos++;
			if(pos == arrayLength-1){
				isSorted[pos] = judge;
				finalBlock = pos;  //最後のブロックの特別処理用
				workers.add(Executors.callable(new MergeSortWorker(first,pos,num++)));
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

//
//	private void markFinalBoolean() {
//		for(int i = first;i <=pos;i++){
//			isSorted[i] = judge;
//		}
//		finalBlock = first;  //最後のブロックの特別処理用
//	}


	class MergeSortWorker implements Runnable{
		int num,nextRight,id;
		int left,right,mid,pattern;
		boolean isNext = true;
		boolean isRest = false;
		Object[] buff;



		public MergeSortWorker(int left,int mid,int right,int num,int pattern){
			this.num = num;
			this.pattern = pattern;
			this.left = left;
			this.mid = mid;
			this.right = right;
			id = num;

		}

		public MergeSortWorker(int left,int right,int num){
			this.num = num;
			this.left = left;
			this.right = right;
			isRest = true;
		}


		public void run(){


			if(!isRest){   //余りの配列じゃなければ,はじめのソートを行う
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
				default :
					break;

				}
				//markBoolean(num,left,right);
				printAll("FIRST MERGE");
			}



			do{
				if(num%2 == 0 ){	//次の仕事の処理もする
					//printAll();
					//偶数、かつfinalNumのとき、停止
					if(left == finalBlock){
						printAll("END");
						return;
					}

					while(isSorted[right] == isSorted[right + 1]){  //監視し続ける。次の文字が違うbooleanになったら実行

						if(right + 1 == finalBlock){//num != 0でなくマージしたい相手が最後の区分であるとき
							for(int i = right + 1;i < arrayLength;i++){
								while(isSorted[right] != isSorted[i]){  //すべて〇じゃなかったら

								}

							}
							merge(left,right, arrayLength-1);
							markBoolean(num,left,arrayLength-1);
							finalBlock = left;  //finalBlcokの更新
							printAll("Not end Thread num");
							return;
						}
					}
					//新しいrightを求める
					for(nextRight = right+1;nextRight < arrayLength - 1;nextRight++){
						while(nextRight == finalBlock){   //現在見ているところがfinalBlockであれば、すべて終わるまで待つ
						}
						if(isSorted[nextRight] == isSorted[nextRight+1]){

							break;
						}
					}

					mid = right;
					right = nextRight;

					merge(left,mid, right);
					markBoolean(num,left,right);
					num = num/2;

					printAll("SECOND");

					if(left == 0 && right == arrayLength-1)  //もし最後の処理であったら
						return;

				}else{//2で割って奇数だったら
					//左側を見続ける
						while(isSorted[left] != isSorted[left - 1]){
							System.out.print("num : "+num+ " waiting"+"for leftEnd  ");
							System.out.print("left : "+left+ " right : "+right);

						}
					//左側の整列が確認されたら
					//num = num/2;
					markBoolean(num,left,right);
					System.out.println("  num : "+num);
					return;
				}
			}while(true);
		}


		private synchronized void printAll() {
			System.out.println(+num+"    left : "+left+"  right : "+right+" temNum : "+id+"finalBlock : "+finalBlock);
			MyArrayUtil.print(array);
			printBoolean();
		}


		private synchronized void printAll(String st) {
			System.out.println(num+ "      "+st);
			System.out.println(+num+"    left : "+left+"  right : "+right+" temNum : "+id+"finalBlock : "+finalBlock);
			MyArrayUtil.print(array);
			printBoolean();
		}

	}

	private synchronized void markBoolean(int num,int left,int right) {
		if(num%2 == 0){	//numが奇数だったらすべてにtrueを代入
			for(int i = left;i <= right;i++){
				isSorted[i] = true;
			}
		}else{
			for(int i = left;i <= right;i++){
				isSorted[i] = false;
			}
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
