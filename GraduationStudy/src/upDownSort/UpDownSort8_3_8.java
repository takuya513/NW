package upDownSort;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * バージョン8_3_6のマージ部分で少なかったらinsertSort
 */


import tools.MyArrayUtil;
import upDownSort.NoNameSort9_2.MergeSortWorker;
/*
 * 最速、引数にはarray,buffは上げといた形
 */
public class UpDownSort8_3_8<E extends Comparable> {
	//private E[] array;
	//private E[] buff;
	private int arrayLength,taskNum;
	private int[] posPointer;//次のmiddle or right を覚えておく配列
	//private AtomicBoolean[] isSorted;   //ソート完了部分と未完成部分の判定用
	private boolean[] isSorted;   //ソート完了部分と未完成部分の判定用
	private ExecutorService executor;
	private LinkedList<Callable<Object>> workers;

	int time = 0; //テスト

	static final int ALLREVERSE = 1;  //指定の範囲の配列がすべて降順のとき
	static final int UPUP = 2;	//指定の範囲の配列が昇順、昇順の組合せのとき
	static final int UPDOWN = 3;	//指定の範囲の配列が昇順、降順の組合せのとき
	static final int DOWNUP = 4;	//指定の範囲の配列が降順、昇順の組合せのとき
	static final int DOWNDOWN = 6;	//指定の範囲の配列が降順、降順の組合せのとき
	static final int REST = 7;		////指定の範囲の配列が余りであり昇順のとき

	public UpDownSort8_3_8(){

	}

	public long sort(E[] array){

		time = 0;
		E[] buff = array.clone();
		long start = System.nanoTime();
		init(array);  //初期設定
		//long start2 = System.nanoTime();

		createTasks(array,buff);	//タスクを生成する
		//long end2 = System.nanoTime();
		startWorks();  //タスクの実行
		long end = System.nanoTime();

		//System.out.println("FIEST  : "+(end2 - start2));
		//System.out.println("setTime: "+time);
		return end - start;
	}

	private void init(Object[] array) {
		//this.array = array;
		//this.buff = array.clone();
		arrayLength = array.length;
		isSorted = new boolean[arrayLength];
		posPointer = new int[arrayLength];
		taskNum = 0;


		Arrays.fill(posPointer, -1);  //posPointerの初期化

		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()-1);
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
	public void createTasks(E[] array, E[] buff){

		int unitCount = 0;
		int pattern = 1;	//昇順昇順、降順降順などの区切った配列のマージパターンの指定用
		int left = 0;
		int mid = 0;
		int right = 0;


		while(right <= arrayLength - 1){


			if( array[right].compareTo(array[right+1]) <= 0){	//昇順なら

				pattern = pattern * (unitCount + 1);

				//long st = System.nanoTime();
				while(array[right].compareTo(array[right+1]) <= 0){  //どこまで昇順か調べる

					if(right+1 >= arrayLength-1){	//最後まで昇順であれば
						//long ed = System.nanoTime();
						//time += ed - st;
						if(unitCount == 0){
							settingTask(array, buff, left,right+1,REST); //仕事をセットする
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

							settingTask(array,buff, left,right+1,ALLREVERSE);  //仕事をセットする
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
					settingTask(array,buff, left,mid,right+1,pattern);  //MergeSortWorkerに仕事を投げる
					return;
				}
			}else{	//二つのセットができたとき

				settingTask(array, buff, left,mid,right,pattern);  //MergeSortWorkerに仕事を投げる



				if(right+1 == arrayLength-1){  //次の一つの要素が配列の終わりなら
					settingTask(array, buff, right+1,right+1,REST);  //MergeSortWorkerに仕事を投げる
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
	private void settingTask(E[] array,E[] buff, int left,int mid,int right,int pattern){
		updatePosPointer(left, right);
		firstIsSorted(left,mid,right);
		workers.add(Executors.callable(new MergeSortWorker(array, buff, left,mid,right,taskNum++,pattern)));

	}

	private void settingTask(E[] array,E[] buff, int pos1,int pos2,int pattern){
		if(pattern == ALLREVERSE)  //余りがすべて降順であったら,もしくは最後の一つであったら
			updateIsSorted(0,pos1,pos2);//oをつける
		else
			updateIsSorted(taskNum,pos1,pos2);//oxをつける
		//updateIsSorted(0,pos1,pos2);//oをつける
		updatePosPointer(pos1, pos2);
		workers.add(Executors.callable(new MergeSortWorker(array, buff, pos1,pos2,taskNum++,pattern)));

	}


	private synchronized void updateIsSorted(int num,int left,int right) {

		if(num%2 == 0){	//numが奇数だったらすべてにtrueを代入
			isSorted[left] = true;
			isSorted[right] = true;
		}else{
			isSorted[left] = false;
			isSorted[right] = false;
		}
	}

	private void firstIsSorted(int left,int mid,int right) {

		isSorted[left] = true;
		isSorted[right] = false;
	}

	//posPointerを更新
	private void updatePosPointer(int pos1, int pos2) {
		posPointer[pos1] = pos2;//次のポジションの保存
		posPointer[pos2] = pos1; //前のポジションも保存
	}




	class MergeSortWorker implements Runnable{
		E[] array;
		E[] buff;
		private int num,id;
		private int left,right,mid,pattern;

		public MergeSortWorker(E[] array2,E[] buff2,int left,int mid,int right,int num,int pattern){
			this.num = num;
			this.pattern = pattern;
			this.left = left;
			this.mid = mid;
			this.right = right;
			this.array = array2;
			this.buff = buff2;
		}


		public MergeSortWorker(E[] array,E[] buff,int left,int right,int num,int pattern){
			this.num = num;
			this.left = left;
			this.right = right;
			this.pattern = pattern;
			this.array = array;
			this.buff = buff;
		}


		public void run(){

			firstMerge();  //最初の様々なパターンのマージ
			expanseMerge();  //順次ソートしていく

		}


		private void expanseMerge() {
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
							merge(array,buff,left,mid,right);
							//printAll("REST");
						}else{
							return;//終了
						}


					}else{//余りでなければ
						if(checkSideIsSorted(right,true)){	//右のソートが終わっていれば
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
					if(checkSideIsSorted(left,false)){  //左のソートが終わっていれば
						//ソート範囲を拡大しmergeする
						mid = left - 1;
						left = posPointer[mid];
						merge(array,buff,left,mid,right);
					}else{ //左のソートが終わっていなければこのタスクは終了
						return;
					}
				}

				//System.out.println("left : "+left+"  right : "+right);
				//MyArrayUtil.print(array);
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

	public void swap(E[] array,int i,int j){
		E temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

//	public void merge(E[] array,E[] buff, int left,int mid,int right){
//		int length = right -  left+1;
//
//	     if (array[mid].compareTo(array[mid+1]) <= 0) {
//             return;
//         }
//
//	 	if(length < 7){
//			for(int i = left;i <= right;i++){
//				for(int j = i; j > left && array[j - 1].compareTo(array[j]) > 0;j--)
//					swap(array,j,j-1);
//			}
//			return;
//		}
//
//		for (int i = left, p = left, q = mid+1; i <= right; i++) {
//			if (q > right || p <= mid && array[p].compareTo(array[q]) <= 0) {
//				buff[i] = array[p++];
//			} else {
//				buff[i] = array[q++];
//			}
//		}
//
//		System.arraycopy(buff, left, array, left, right-left+1);
//	}

	@SuppressWarnings("unchecked")
	public void merge(E[] array, E[] buff, int left,int mid,int right){
		if (array[mid].compareTo(array[mid+1]) <= 0) {
			return;
		}




		int i = left,j = mid + 1,k = i;
		int add = 1;
		int lastPos = i;
		int endPos = mid;


		//二乗で探索していく
		while(i <= mid && j <= right){

			if(array[i].compareTo(array[j]) < 0){

				lastPos = i;
				while(array[i].compareTo(array[j]) < 0){
					add *=2;
					i+=add;
					endPos = i;
					if(i > mid){
						endPos = mid;
						break;
					}
				}
				i-=add;

				if(lastPos != i){  //ポジション(i)の位置が変化したら
					System.arraycopy(array,lastPos,buff,k,i - lastPos);
					k+=i - lastPos;
//					i++;
//					for(int n = lastPos;n < i;n++){
//						buff[k++] = array[n];
//					}
					lastPos = i;
				}


				i = binarySearch(array,lastPos,endPos,array[j],endPos);
//				while(array[i].compareTo(array[j]) < 0 && i <= lastPos){
//					buff[k++] = array[i++];
//				}


				if(add >= 8){

					System.arraycopy(array,lastPos,buff,k,i-lastPos);
					k+=i-lastPos;
				}else{
//
					for(int n = lastPos;n < i;n++)
						buff[k++] = array[n];

				}

				buff[k++] = array[j++];
				add = 1;

			}else{
				buff[k++] = array[j++];
			}

		}

		if(i <= mid){
			System.arraycopy(array,i, buff, k, mid-i+1);
		}
		if(j <= right){
			System.arraycopy(array,j, buff, k, right - j +1);
		}
		System.arraycopy(buff, left, array, left, right-left+1);


	}


	public int binarySearch(E[] array,int left,int right,E key,int end){

		if(right - left == 0){
			if(array[left].compareTo(key) <= 0)
				return left+1;
			return left;
		}

		if(left > right)
			return left;

		int mid = (left + right) / 2;

		if(array[mid].compareTo(key) < 0)
			return binarySearch(array,mid+1,right,key,end);
		else if(array[mid].compareTo(key) == 0)
			return binarySearch(array,left+1,right,key,end);
		else
			return binarySearch(array,left,mid-1,key,end);
	}


	private void reverse(E[] array, E[] buff, int left, int right) {

		for(int i = left,j = right;i <= right;i++,j--)
			buff[i] = array[j];

//		for(int i = left;i <= right;i++){
//			array[i] = buff[i];
//		}

		System.arraycopy(buff, left, array, left, right-left+1);

	}


	public void mergeUpDown(E[] array, E[] buff, int left,int mid,int right){

		//int length = right-  left+1;



	 	if(right-  left < 7){
			for(int i = left;i <= right;i++){
				for(int j = i; j > left && array[j - 1].compareTo(array[j]) > 0;j--)
					swap(array,j,j-1);
			}
			return;
		}

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
//		for(i = left;i <= right; i++)
//			array[i] = buff[i];

		System.arraycopy(buff, left, array, left, right-left+1);
	}

	public void mergeDownUp(E[] array, E[] buff, int left,int mid,int right){
		//int length = right-  left+1;



	 	if(right-  left < 7){
			for(int i = left;i <= right;i++){
				for(int j = i; j > left && array[j - 1].compareTo(array[j]) > 0;j--)
					swap(array,j,j-1);
			}
			return;
		}


		int i = mid,j = mid+1,k = left;

		while(i >= left && j <= right) {
			if( array[i].compareTo(array[j]) < 0)
				buff[k++] = array[i--];
			else
				buff[k++] = array[j++];
		}

		while(i >= left)
			buff[k++] = array[i--];
		while(j <= right)
			buff[k++] = array[j++];
//		for(i = left;i <= right; i++)
//			array[i] = buff[i];

		System.arraycopy(buff, left, array, left, right-left+1);
	}

	public void mergeDownDown(E[] array, E[] buff, int left,int mid,int right){
		//int length = right -  left+1;



	 	if(right -  left < 7){
			for(int i = left;i <= right;i++){
				for(int j = i; j > left && array[j - 1].compareTo(array[j]) > 0;j--)
					swap(array,j,j-1);
			}
			return;
		}


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
//		for(i = left;i <= right; i++)
//			array[i] = buff[i];

		System.arraycopy(buff, left, array, left, right-left+1);
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
