package upDownSort;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;




import tools.MyArrayUtil;
/*
 * volatile を使っている
 */
/*
 * 同期取れていない、バグあり
 */
import upDownSort.NoNameSort9_2.MergeSortWorker;


public class UpDownSort8_2<E extends Comparable> {
	private E[] array;
	private int arrayLength,taskNum;
	private int[] posPointer;//次のmiddle or right を覚えておく配列
	volatile boolean[] isSorted;   //ソート完了部分と未完成部分の判定用
	private ExecutorService executor;
	private LinkedList<Callable<Object>> workers;

	static final int ALLREVERSE = 1;  //指定の範囲の配列がすべて降順のとき
	static final int UPUP = 2;	//指定の範囲の配列が昇順、昇順の組合せのとき
	static final int UPDOWN = 3;	//指定の範囲の配列が昇順、降順の組合せのとき
	static final int DOWNUP = 4;	//指定の範囲の配列が降順、昇順の組合せのとき
	static final int DOWNDOWN = 6;	//指定の範囲の配列が降順、降順の組合せのとき
	static final int REST = 7;		////指定の範囲の配列が余りであり昇順のとき

	public UpDownSort8_2(){

	}

	public void sort(E[] array){
		//long start = System.currentTimeMillis();
		init(array);  //初期設定
		//long stop = System.currentTimeMillis();
		//System.out.println("FIRSTTIME : "+(stop - start));

		makingWorks();	//タスクを生成する

		//printAll();
		startWorks();  //タスクの実行

		//printAll();

	}

	private void init(E[] array) {
		this.array = array;
		arrayLength = array.length;
		isSorted = new boolean[arrayLength];
		posPointer = new int[arrayLength];
		taskNum = 0;


		Arrays.fill(posPointer, -1);  //posPointerの初期化


		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		workers = new LinkedList<Callable<Object>>();
	}

	private void startWorks() {
		try {
			executor.invokeAll(workers);
			executor.shutdown();
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
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
			for(int i = 0;i < 2;i++){		//for文で回す
				if(array[pos].compareTo(array[pos+1]) <= 0){	//昇順なら
					pattern = pattern * (i + 1);
					while(array[pos].compareTo(array[pos+1]) <= 0){  //どこまで上に上がるか
						if(pos+1 >= arrayLength-1){
							if(i == 0){	//for文が一週目なら
								settingTask(left,pos+1,REST); //MergeSortWorkerに仕事を投げる
								return;
							}else{
								pos++;
								break;
							}
						}
						pos++;  //次に進める
					}
				}else {//降順なら
					pattern = pattern * (i + 2);
					while(array[pos].compareTo(array[pos+1]) > 0){//どこまで下に下がるか
						//もしiが0　かつ　最後まで行ってしまったらMergeWorkerに仕事を投げてreturn
						if(pos+1 >= arrayLength-1){
							if(i == 0){	//for文が一週目なら
								settingTask(left,pos+1,ALLREVERSE);  //MergeSortWorkerに仕事を投げる
								return;
							}else{
								pos++;
								break;
							}
						}
						pos++;
					}
				}

				if(i == 0){  //まだ一週目なら様々な処理をする
					mid = pos;
					pos++; //次に進める
					if(pos == arrayLength - 1){
						insertSort(left,pos);
						settingTask(left,pos,REST); //MergeSortWorkerに仕事を投げる
						return;
					}
				}
			}//for文終了

			settingTask(left,mid,pos,pattern);  //MergeSortWorkerに仕事を投げる
			pos++;  //次に進める

			if(pos == arrayLength-1){
				settingTask(pos,pos,REST);  //MergeSortWorkerに仕事を投げる
				return;
			}
		}//whlie文終了
	}

	//二つセットで仕事がある場合
	private void settingTask(int left,int mid,int right,int pattern){
		updatePosPointer(left, right);
		markBooleanFirst(left,mid,right);
		workers.add(Executors.callable(new MergeSortWorker(left,mid,right,taskNum++,pattern)));

	}

	private void settingTask(int pos1,int pos2,int pattern){
		if(pattern == ALLREVERSE)  //余りがすべて降順であったら,もしくは最後の一つであったら
			updateIsSorted(0,pos1,pos2);//oをつける
		else
			updateIsSorted(taskNum,pos1,pos2);//oxをつける
		//updateIsSorted(0,pos1,pos2);//oをつける
		updatePosPointer(pos1, pos2);
		workers.add(Executors.callable(new MergeSortWorker(pos1,pos2,taskNum++,pattern)));

	}


	private synchronized void updateIsSorted(int num,int left,int right) {
//		if(num%2 == 0){	//numが奇数だったらすべてにtrueを代入
//			for(int i = left;i <= right;i++){
//				isSorted[i] = true;
//			}
//		}else{
//			for(int i = left;i <= right;i++){
//				isSorted[i] = false;
//			}
//		}

		if(num%2 == 0){	//numが奇数だったらすべてにtrueを代入
			isSorted[left] = true;
			isSorted[right] = true;
		}else{
			isSorted[left] = false;
			isSorted[right] = false;
		}
	}

	private void markBooleanFirst(int left,int mid,int right) {
//		for(int i = left;i <= mid;i++){
//			isSorted[i] = true;
//		}
//		for(int i = mid+1;i <= right;i++){
//			isSorted[i] = false;
//		}
		isSorted[left] = true;
		isSorted[right] = false;
	}

	//posPointerを更新
	private void updatePosPointer(int pos1, int pos2) {
		posPointer[pos1] = pos2;//次のポジションの保存
		posPointer[pos2] = pos1; //前のポジションも保存
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
		private int num,id;
		private int left,right,mid,pattern;

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

			//long start = System.currentTimeMillis();
			firstMerge();  //最初の様々なパターンのマージ
			//long stop = System.currentTimeMillis();
			//System.out.println("FIRSTMERGETIME : "+(stop -start));
			//printAll("FIRST");
			//long start2 = System.currentTimeMillis();
			expanseMerge();  //順次ソートしていく
			//long stop2 = System.currentTimeMillis();
			//System.out.println("[["+ id + "]]  SECONDTIME : "+((stop2 - start2)));
		}


		private void expanseMerge() {
			do{

				//numが偶数か奇数で分ける
				//偶数であれば
				if(num % 2 == 0){

					//余りであれば　　　このrightがarrayLength - 1で判断する
					if(right == arrayLength - 1){
						//左側を監視し続ける
//						while(isSorted[left - 1].get() != isSorted[left].get());
//						isSorted[left].set(!isSorted[left].get()); //同期のため
//
//						//左がoになったらソート範囲を拡大しmergeする
//						mid = left - 1;
//						left = posPointer[mid];
//						merge(left,mid,right);

						updateIsSorted(1,left,right);	//IsSortedの更新
						if(isSorted[left - 1] != isSorted[left]){  //左のソートが終わっていれば
							isSorted[left] = !isSorted[left];
							//ソート範囲を拡大しmergeする
							mid = left - 1;
							left = posPointer[mid];

							merge(left,mid,right);
						}else{
							return;//終了
						}

						updateAll();

					}else{//余りでなければ
						if(isSorted[right] != isSorted[right + 1]){	//右のソートが終わっていれば
							//ソート範囲を拡大しmergeする
							isSorted[right] = (!isSorted[right]); //同期のため
							mid = right;
							right = posPointer[mid+1];
							merge(left,mid,right);
						}else{ //右のソートが終わっていなければ終了
							return;//終了
						}
					}
				}else{  //奇数であれば
					if(isSorted[left - 1] != isSorted[left]){  //左のソートが終わっていれば
						isSorted[left] = !isSorted[left];
						//ソート範囲を拡大しmergeする
						mid = left - 1;
						left = posPointer[mid];

						merge(left,mid,right);

					}else{ //左のソートが終わっていなければこのタスクは終了
						return;
					}
				}

				if(left == 0 && right == arrayLength - 1){	//最後のソートであったら終了
					return;
				}

				updateAll();	//isSorted,posPointer,numの更新
			}while(true);
		}


		private void updateAll() {
			//numの更新
			if(num % 2 == 0)
				num = num / 2;
			else
				num  = (num - 1) / 2;
			updatePosPointer(left,right);//posPointerの更新
			updateIsSorted(num,left,right);	//IsSortedの更新
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
				break;
			case REST:
				return;
			default :
				break;

			}

			updateIsSorted(num,left,right);  //IsSortedの更新
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

	private synchronized void printAll() {
		//System.out.println(+num+"    left : "+left+"  right : "+right+" id : "+id+"num : "+num);
		MyArrayUtil.print(array);
		MyArrayUtil.print(posPointer);
		printBoolean();
		System.out.println();
	}
}
