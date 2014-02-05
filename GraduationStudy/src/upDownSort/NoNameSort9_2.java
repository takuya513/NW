package upDownSort;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;


import tools.MyArrayUtil;
/*
 * 同期取れていない、バグあり
 */

public class NoNameSort9_2<E extends Comparable> {
	private E[] array;
	private int arrayLength,taskNum;
	private int[] posPointer;//次のmiddle or right を覚えておく配列
	private AtomicBoolean[] isSorted;   //ソート完了部分と未完成部分の判定用
	private ExecutorService executor;
	private LinkedList<Callable<Object>> workers;

	static final int ALLREVERSE = 1;  //指定の範囲の配列がすべて降順のとき
	static final int UPUP = 2;	//指定の範囲の配列が昇順、昇順の組合せのとき
	static final int UPDOWN = 3;	//指定の範囲の配列が昇順、降順の組合せのとき
	static final int DOWNUP = 4;	//指定の範囲の配列が降順、昇順の組合せのとき
	static final int DOWNDOWN = 6;	//指定の範囲の配列が降順、降順の組合せのとき
	static final int REST = 7;		////指定の範囲の配列が余りであり昇順のとき

	public NoNameSort9_2(){

	}

	public void sort(E[] array){
		init(array);  //初期設定
		//printAll();
		createTasks();	//タスクを生成する
		//printAll();
		invokeTasks();  //タスクの実行
		//printAll();
	}

	private void init(E[] array) {
		this.array = array;
		arrayLength = array.length;
		isSorted = new AtomicBoolean[arrayLength];
		posPointer = new int[arrayLength];
		taskNum = 0;

		Arrays.fill(posPointer, -1);  //posPointerの初期化

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
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void createTasks(){
		int unitCount = 0;
		int pattern = 1;	//昇順昇順、降順降順などの区切った配列のマージパターンの指定用
		int left = 0;
		int mid = 0;
		int right = 0;

		while(right <= arrayLength - 1){

			if(array[right].compareTo(array[right+1]) <= 0){	//昇順なら
				pattern = pattern * (unitCount + 1);
				while(array[right].compareTo(array[right+1]) <= 0){  //どこまで昇順か調べる
					if(right+1 >= arrayLength-1){	//最後まで昇順であれば
						if(unitCount == 0){
							settingTask(left,right+1,REST); //仕事をセットする
							return;
						}else{
							right++;	//次に進める
							break;
						}
					}
					right++;  //次に進める
				}

			}else {//降順なら
				pattern = pattern * (unitCount + 2);
				while(array[right].compareTo(array[right+1]) > 0){//どこまで降順か調べる
					if(right+1 >= arrayLength-1){	//最後まで降順であれば
						if(unitCount == 0){		//二つのセットでなければ
							settingTask(left,right+1,ALLREVERSE);  //仕事をセットする
							return;
						}else{	//二つのセットであれば
							right++;	//次に進める
							break;
						}
					}
					right++;	//次に進める
				}
			}

			if(unitCount == 0){  //一つ目の昇順or降順を調べたとき
				mid = right;
				unitCount++;
				if(right+1 == arrayLength - 1){	//次の一つの要素が配列の終わりなら
					if(pattern == 1) //patternで判定する
						pattern = UPDOWN;
					else
						pattern = DOWNUP;
					settingTask(left,mid,right+1,pattern);  //MergeSortWorkerに仕事を投げる
					return;
				}
			}else{	//二つのセットができたとき
				settingTask(left,mid,right,pattern);  //MergeSortWorkerに仕事を投げる
				if(right+1 == arrayLength-1){  //次の一つの要素が配列の終わりなら
					settingTask(right+1,right+1,REST);  //MergeSortWorkerに仕事を投げる
					return;
				}
				pattern = 1; //初期化
				unitCount = 0;	//初期化
				left = right+1;
			}
			right++;  //次に進める

		}//whlie文終了
	}


	//二つセットで仕事がある場合
	private void settingTask(int left,int mid,int right,int pattern){
		updatePosPointer(left, right);
		firstIsSorted(left,mid,right);
		workers.add(Executors.callable(new MergeSortWorker(left,mid,right,taskNum++,pattern)));

	}

	private void settingTask(int pos1,int pos2,int pattern){
		if(pattern == ALLREVERSE || pos1 == pos2)  //余りがすべて降順であったら,もしくは最後の一つであったら
			updateIsSorted(0,pos1,pos2);//oをつける
		else
			updateIsSorted(taskNum,pos1,pos2);//oxをつける

		updatePosPointer(pos1, pos2);
		workers.add(Executors.callable(new MergeSortWorker(pos1,pos2,taskNum++,pattern)));

	}


	private synchronized void updateIsSorted(int num,int left,int right) {

//		if(num%2 == 0){	//numが奇数だったらすべてにtrueを代入
//			isSorted[left].set(true);
//			isSorted[right].set(true);
//		}else{
//			isSorted[left].set(false);
//			isSorted[right].set(false);
//		}
		if(num%2 == 0){	//numが奇数だったらすべてにtrueを代入
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

	//posPointerを更新
	private synchronized void updatePosPointer(int pos1, int pos2) {
		posPointer[pos1] = pos2;//次のポジションの保存
		posPointer[pos2] = pos1; //前のポジションも保存
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
			firstMerge();  //最初の様々なパターンのマージ

			expanseMerge();  //順次ソートしていく
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
			updateIsSorted(num,left,right);  //IsSortedの更新
		}

		private void expanseMerge() {
			do{
				//numが偶数か奇数で分ける
				//偶数であれば
				if(num % 2 == 0){
					//最後のセットであるならば
					if(right == arrayLength - 1){
						//左側を監視し続ける
						//while(isSorted[left - 1].get() != isSorted[left].get());

						//左がoになったらソート範囲を拡大しmergeする
//						mid = left - 1;
//						left = posPointer[mid];
//						merge(left,mid,right);
						updateIsSorted(1,left,right);	//IsSortedの更新
						return;
					}else{//余りでなければ
						if(isSorted[right].get() != isSorted[right + 1].get()){	//右のソートが終わっていれば
							//ソート範囲を拡大しmergeする
							mid = right;
							right = posPointer[mid+1];
							merge(left,mid,right);

						}else //右のソートが終わっていなければ終了
							return;//終了
					}
				}else{  //奇数であれば
					if(isSorted[left - 1].get() != isSorted[left].get()){  //左のソートが終わっていれば
						//ソート範囲を拡大しmergeする
						mid = left - 1;
						left = posPointer[mid];
						merge(left,mid,right);
					}else //左のソートが終わっていなければこのタスクは終了
						return;
				}

				if(left == 0 && right == arrayLength - 1)	//最後のソートであったら終了
					return;

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



		//テスト用
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

	//テスト用
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
