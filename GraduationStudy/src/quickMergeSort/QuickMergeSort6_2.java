package quickMergeSort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


import tools.MyArrayUtil;
import tools.TestTools;
/*
 * timSortである程度降順、昇順の場合高速化
 */
public class QuickMergeSort6_2<E extends Comparable> {
	private int[] posPointer;//次のmiddle or right を覚えておく配列
	private boolean[] isSorted;   //ソート完了部分と未完成部分の判定用   //ソート完了部分と未完成部分の判定用
	private final int SMALL = 20;
	private int taskNum;

	ExecutorService executor;
	int threadsNum, arrayLength, sortSection, left, right;
	int restPivot; //マージするときに余った部分の仕切り
	LinkedList<Callable<Object>> workers;


	public QuickMergeSort6_2(){




	}

	public long sort(E[] array){

		init(array,Runtime.getRuntime().availableProcessors()-1);
		long start = System.nanoTime();
		quickMergeSort(array);
		long end =System.nanoTime();

		return end - start;



	}

	private void init(E[] array,int threadsNum) {
		arrayLength = array.length;

		sortSection = arrayLength / (threadsNum * 2);
		left = 0;  right = sortSection - 1;
		restPivot = -1;
		taskNum = 0;

		executor = Executors.newFixedThreadPool(threadsNum);
		workers = new LinkedList<Callable<Object>>();

		isSorted = new boolean[arrayLength];
		posPointer = new int[arrayLength];

		Arrays.fill(posPointer, -1);  //posPointerの初期化
	}

	private void quickMergeSort(E[] array){
		//クイックソートをする
		while(right < arrayLength-1){
			//まずisSorted,posPointerの設定
			settingWork(array,left,right);
			left = right + 1;
			right =  sortSection + right;
		}
		//しかし余りは特別に多少処理するだけでよい
		settingWork(array, left,arrayLength-1);

		invokeTasks();
	}

	private void invokeTasks() {
		try {
			executor.invokeAll(workers);  //workersの仕事を実行し、終わるまで待機
			executor.shutdown();
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	private void settingWork(E[] array, int left,int right){
		updatePosPointer(left, right);
		firstIsSorted(left,right);
		workers.add(Executors.callable(new QuickMergeSortWorker(array,left,right,taskNum++)));

	}

	private  void updateIsSorted(int num,int left,int right) {
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

	private void firstIsSorted(int left,int right) {
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

	class QuickMergeSortWorker implements Runnable{
		E[] array;

		private int num;
		private int left,right,mid;

		public QuickMergeSortWorker(E[] array, int left,int right,int num){
			this.num = num;
			this.left = left;
			this.right = right;
			this.array = array;

		}


		public void run(){
			//はじめにその区分のクイックソート,その後isSortedの更新
			Arrays.sort(array,left,right + 1);
			//quickSort(array,left,right);
			//Arrays.sort(array);
			updateIsSorted(num,left,right);
			//printAll("FIRST");
			expanseMerge(array);  //順次ソートしていく
		}


		private void expanseMerge(E[] array) {
			do{

				//numが偶数か奇数で分ける
				//偶数であれば
				if(num % 2 == 0){

					//余りであれば　　　このrightがarrayLength - 1で判断する
					if(right == arrayLength - 1){

						updateIsSorted(1,left,right);	//IsSortedの更新
						if(checkSideIsSorted(left,false)){  //左のソートが終わっていれば
							//ソート範囲を拡大しmergeする
							mid = left - 1;
							left = posPointer[mid];
							Arrays.sort(array,left,right+1);
							//merge(array,,left,mid,right);
							//printAll("REST");
						}else{
							return;//終了
						}


					}else{//余りでなければ
						if(checkSideIsSorted(right,true)){	//右のソートが終わっていれば
							//ソート範囲を拡大しmergeする
							mid = right;
							right = posPointer[mid+1];
							Arrays.sort(array,left,right+1);
							//merge(array, , left,mid,right);
							//printAll("GUUU");
						}else{ //右のソートが終わっていなければ終了
							return;//終了
						}
					}
				}else{  //奇数であれば
					if(checkSideIsSorted(left,false)){  //左のソートが終わっていれば
						//ソート範囲を拡大しmergeする
						mid = left - 1;
						left = posPointer[mid];
						Arrays.sort(array,left,right+1);
						//merge(array, , left,mid,right);
						//printAll("KISU");
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


		//テスト用
		private synchronized void printAll() {
			System.out.println(+num+"    left : "+left+"  right : "+right+"num : "+num);
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

	private synchronized boolean checkSideIsSorted(int num,boolean isOdd){
		if(isOdd){  //右側を見るとき
			if(isSorted[num] != isSorted[num + 1]){	//右のソートが終わっていれば
				isSorted[num] = !isSorted[num]; //同期のため
				return true;
			}
			return false;
		}else{	//左側を見るとき
			if(isSorted[num - 1] != isSorted[num]){  //左のソートが終わっていれば
				isSorted[num] = !isSorted[num];
				return true;
			}
			return false;
		}

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




}
