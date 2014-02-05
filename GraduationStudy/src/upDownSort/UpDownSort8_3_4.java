package upDownSort;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * 完全な同期化をめざす
 */


import tools.MyArrayUtil;
import upDownSort.NoNameSort9_2.MergeSortWorker;

public class UpDownSort8_3_4<E extends Comparable> {
	//private E[] array;
	private int arrayLength,taskNum;
	//private int[] posPointer;//次のmiddle or right を覚えておく配列
	//private AtomicBoolean[] isSorted;   //ソート完了部分と未完成部分の判定用
	//private boolean[] isSorted;   //ソート完了部分と未完成部分の判定用
	private ExecutorService executor;
	private LinkedList<Callable<Object>> workers;

	static final int ALLREVERSE = 1;  //指定の範囲の配列がすべて降順のとき
	static final int UPUP = 2;	//指定の範囲の配列が昇順、昇順の組合せのとき
	static final int UPDOWN = 3;	//指定の範囲の配列が昇順、降順の組合せのとき
	static final int DOWNUP = 4;	//指定の範囲の配列が降順、昇順の組合せのとき
	static final int DOWNDOWN = 6;	//指定の範囲の配列が降順、降順の組合せのとき
	static final int REST = 7;		////指定の範囲の配列が余りであり昇順のとき

	public UpDownSort8_3_4(){

	}

	public long sort(E[] array){
		init(array);  //初期設定
		E[] buff = array.clone();
		int[] posPointer = new int[array.length];
		boolean[] isSorted = new boolean[array.length];

		long start = System.nanoTime();
		//long start2 = System.nanoTime();

		createTasks(array,buff,posPointer,isSorted);	//タスクを生成する
		//long end2 = System.nanoTime();
		startWorks();  //タスクの実行
		long end = System.nanoTime();

		//System.out.println("FIEST  : "+(end2 - start2));
		//System.out.println("SECON  : "+(end - start));
		return end - start;
	}

	private void init(E[] array) {
		//this.array = array;
		arrayLength = array.length;
		taskNum = 0;



		//executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		executor = Executors.newFixedThreadPool(1);
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
	public void createTasks(E[] array, E[] buff, int[] posPointer, boolean[] isSorted){

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
							settingTask(array, buff, isSorted, posPointer, left,right+1,REST); //仕事をセットする
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
							settingTask(array,buff, isSorted, posPointer, left,right+1,ALLREVERSE);  //仕事をセットする
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
					settingTask(array,buff, isSorted, posPointer, left,mid,right+1,pattern);  //MergeSortWorkerに仕事を投げる
					return;
				}
			}else{	//二つのセットができたとき
				settingTask(array, buff, isSorted, posPointer, left,mid,right,pattern);  //MergeSortWorkerに仕事を投げる
				if(right+1 == arrayLength-1){  //次の一つの要素が配列の終わりなら
					settingTask(array, buff, isSorted, posPointer, right+1,right+1,REST);  //MergeSortWorkerに仕事を投げる
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
	private void settingTask(E[] array, E[] buff,boolean[] isSorted,int[] posPointer,int left,int mid,int right,int pattern){
		updatePosPointer(posPointer, left, right);
		firstIsSorted(isSorted, left,mid,right);
		workers.add(Executors.callable(new MergeSortWorker(array, buff, isSorted,posPointer,left,mid,right,taskNum++,pattern)));

	}

	private void settingTask(E[] array,E[] buff,boolean[] isSorted,int[] posPointer, int pos1,int pos2,int pattern){
		if(pattern == ALLREVERSE)  //余りがすべて降順であったら,もしくは最後の一つであったら
			updateIsSorted(isSorted, 0,pos1,pos2);//oをつける
		else
			updateIsSorted(isSorted, taskNum,pos1,pos2);//oxをつける
		//updateIsSorted(0,pos1,pos2);//oをつける
		updatePosPointer(posPointer, pos1, pos2);
		workers.add(Executors.callable(new MergeSortWorker(array, buff, isSorted,posPointer,pos1,pos2,taskNum++,pattern)));

	}


	private synchronized void updateIsSorted(boolean[] isSorted,int num,int left,int right) {

		if(num%2 == 0){	//numが奇数だったらすべてにtrueを代入
			isSorted[left] = true;
			isSorted[right] = true;
		}else{
			isSorted[left] = false;
			isSorted[right] = false;
		}
	}

	private void firstIsSorted(boolean[] isSorted,int left,int mid,int right) {

		isSorted[left] = true;
		isSorted[right] = false;
	}

	//posPointerを更新
	private void updatePosPointer(int[] posPointer,int pos1, int pos2) {
		posPointer[pos1] = pos2;//次のポジションの保存
		posPointer[pos2] = pos1; //前のポジションも保存
	}




	class MergeSortWorker implements Runnable{
		E[] array;
		E[] buff;
		boolean[] isSorted;
		int[] posPointer;
		private int num,id;
		private int left,right,mid,pattern;

		public MergeSortWorker(E[] array,E[] buff,boolean[] isSorted, int[] posPointer, int left,int mid,int right,int num,int pattern){
			this.num = num;
			this.pattern = pattern;
			this.left = left;
			this.mid = mid;
			this.right = right;
			this.array = array;
			this.buff = buff;
			this.isSorted = isSorted;
			this.posPointer = posPointer;
		}


		public MergeSortWorker(E[] array,E[] buff,boolean[] isSorted, int[] posPointer, int left,int right,int num,int pattern){
			this.num = num;
			this.left = left;
			this.right = right;
			this.pattern = pattern;
			this.array = array;
			this.buff = buff;
			this.isSorted = isSorted;
			this.posPointer = posPointer;
		}


		public void run(){

			//long start = System.nanoTime();
			firstMerge();  //最初の様々なパターンのマージ
			//long end = System.nanoTime();
			//System.out.println("first  : "+(end - start));

			//long start2 = System.nanoTime();
			expanseMerge();  //順次ソートしていく
			//long end2 = System.nanoTime();
			//System.out.println("SSSS"+num+"  : "+(end2 - start2));

		}


		private void expanseMerge() {
			do{

				//numが偶数か奇数で分ける
				//偶数であれば
				if(num % 2 == 0){

					//余りであれば　　　このrightがarrayLength - 1で判断する
					if(right == arrayLength - 1){

						updateIsSorted(isSorted, 1,left,right);	//IsSortedの更新
						if(checkSideIsSorted(isSorted, left,false)){  //左のソートが終わっていれば
							//ソート範囲を拡大しmergeする
							mid = left - 1;
							left = posPointer[mid];
							merge(array,buff,left,mid,right);
							//printAll("REST");
						}else{
							return;//終了
						}


					}else{//余りでなければ
						if(checkSideIsSorted(isSorted, right,true)){	//右のソートが終わっていれば
							//ソート範囲を拡大しmergeする
							mid = right;
							right = posPointer[mid+1];
							merge(array,buff,left,mid,right);
							//printAll("GUUU");
						}else{ //右のソートが終わっていなければ終了
							return;//終了
						}
					}
				}else{  //奇数であれば
					if(checkSideIsSorted(isSorted, left,false)){  //左のソートが終わっていれば
						//ソート範囲を拡大しmergeする
						mid = left - 1;
						left = posPointer[mid];
						merge(array,buff,left,mid,right);
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
			updatePosPointer(posPointer, left,right);//posPointerの更新
			updateIsSorted(isSorted, num,left,right);	//IsSortedの更新
		}

		//posPointerを更新
		private void updatePosPointer(int[] posPointer,int pos1, int pos2) {
			posPointer[pos1] = pos2;//次のポジションの保存
			posPointer[pos2] = pos1; //前のポジションも保存
		}

		private void firstMerge() {
			switch(pattern){
			case UPUP:
				merge(array,buff,left,mid,right);
				break;
			case UPDOWN:
				mergeUpDown(array,buff,left,mid,right);
				break;
			case DOWNUP:
				mergeDownUp(array,buff,left,mid,right);
				break;
			case DOWNDOWN:
				mergeDownDown(array,buff,left,mid,right);
				break;
			case ALLREVERSE:
				reverse(array,buff,left,right);
				break;
			case REST:
				return;
			default :
				break;

			}

			updateIsSorted(isSorted, num,left,right);  //IsSortedの更新
		}



//		private synchronized void printAll() {
//			System.out.println(+num+"    left : "+left+"  right : "+right+"num : "+num);
//			MyArrayUtil.print(array);
//			MyArrayUtil.print(posPointer);
//			printBoolean();
//			System.out.println();
//		}
//
//		private synchronized void printAll(String st) {
//			System.out.println("[["+id+"]]"+"      "+st);
//			System.out.println(+num+"    left : "+left+"  mid : +"+mid+"  right : "+right+" id : "+id+"num : "+num);
//			MyArrayUtil.print(array);
//			MyArrayUtil.print(posPointer);
//			printBoolean();
//			System.out.println();
//		}

	}

	private synchronized boolean checkSideIsSorted(boolean[] isSorted,int num,boolean isOdd){
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

	public void merge(E[] array, E[] buff, int left,int mid,int right){
		//int i = left,j = mid + 1,k = left;

	     if (array[mid].compareTo(array[mid+1]) <= 0) {
             return;
         }


		for (int i = left, p = left, q = mid+1; i <= right; i++) {
			if (q > right || p <= mid && array[p].compareTo(array[q]) <= 0) {
				buff[i] = array[p++];
			} else {
				buff[i] = array[q++];
			}
		}


		System.arraycopy(buff, left, array, left, right-left+1);

	}




	private void reverse(E[] array, E[] buff, int left, int right) {

		for(int i = left,j = 0;i <= right;i++,j++)
			buff[i] = array[right-j];

		System.arraycopy(buff, left, array, left, right-left+1);

	}


	public void mergeUpDown(E[] array, E[] buff, int left,int mid,int right){
		int i = left,j = right,k = left;

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

		System.arraycopy(buff, left, array, left, right-left+1);
	}

	public void mergeDownUp(E[] array, E[] buff, int left,int mid,int right){
		int i = mid,j = mid+1,k = left;

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

		System.arraycopy(buff, left, array, left, right-left+1);
	}

	public void mergeDownDown(E[] array, E[] buff, int left,int mid,int right){
		int i = mid,j = right,k = left;

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

		System.arraycopy(buff, left, array, left, right-left+1);
	}




}
