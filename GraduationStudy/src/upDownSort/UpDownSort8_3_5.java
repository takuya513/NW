package upDownSort;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * バージョン8_3_3のダウンキャスト防止ソートand buffの修正
 */


import tools.MyArrayUtil;
import upDownSort.NoNameSort9_2.MergeSortWorker;
/*
 * 最速、引数にはarray,buffは上げといた形
 */
public class UpDownSort8_3_5<E extends Comparable> {
	//private E[] array;
	//private E[] buff;
	private int arrayLength,taskNum;
	private int[] posPointer;//次のmiddle or right を覚えておく配列
	//private AtomicBoolean[] isSorted;   //ソート完了部分と未完成部分の判定用
	private boolean[] isSorted;   //ソート完了部分と未完成部分の判定用
	private ExecutorService executor;
	private LinkedList<Callable<Object>> workers;

	static final int ALLREVERSE = 1;  //指定の範囲の配列がすべて降順のとき
	static final int UPUP = 2;	//指定の範囲の配列が昇順、昇順の組合せのとき
	static final int UPDOWN = 3;	//指定の範囲の配列が昇順、降順の組合せのとき
	static final int DOWNUP = 4;	//指定の範囲の配列が降順、昇順の組合せのとき
	static final int DOWNDOWN = 6;	//指定の範囲の配列が降順、降順の組合せのとき
	static final int REST = 7;		////指定の範囲の配列が余りであり昇順のとき

	public UpDownSort8_3_5(){

	}

	public long sort(E[] array2){

		Object[] array = array2;
		Object[] buff = new Object[array.length];
		long start = System.nanoTime();
		init(array);  //初期設定
		//long start2 = System.nanoTime();

		createTasks(array,buff);	//タスクを生成する
		//long end2 = System.nanoTime();
		startWorks();  //タスクの実行
		long end = System.nanoTime();

		//System.out.println("FIEST  : "+(end2 - start2));
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
	public void createTasks(Object[] array, Object[] buff){

		int unitCount = 0;
		int pattern = 1;	//昇順昇順、降順降順などの区切った配列のマージパターンの指定用
		int left = 0;
		int mid = 0;
		int right = 0;

		while(right <= arrayLength - 1){

			if(((Comparable) array[right]).compareTo(array[right+1]) <= 0){	//昇順なら
				pattern = pattern * (unitCount + 1);
				while(((Comparable) array[right]).compareTo(array[right+1]) <= 0){  //どこまで昇順か調べる
					if(right+1 >= arrayLength-1){	//最後まで昇順であれば
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
				while(((Comparable) array[right]).compareTo(array[right+1]) > 0){//どこまで降順か調べる
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
	private void settingTask(Object[] array,Object[] buff, int left,int mid,int right,int pattern){
		updatePosPointer(left, right);
		firstIsSorted(left,mid,right);
		workers.add(Executors.callable(new MergeSortWorker(array, buff, left,mid,right,taskNum++,pattern)));

	}

	private void settingTask(Object[] array,Object[] buff, int pos1,int pos2,int pattern){
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
		Object[] array;
		Object[] buff;
		private int num,id;
		private int left,right,mid,pattern;

		public MergeSortWorker(Object[] array2,Object[] buff2,int left,int mid,int right,int num,int pattern){
			this.num = num;
			this.pattern = pattern;
			this.left = left;
			this.mid = mid;
			this.right = right;
			this.array = array2;
			this.buff = buff2;
		}


		public MergeSortWorker(Object[] array,Object[] buff,int left,int right,int num,int pattern){
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

	public void merge(Object[] array, Object[] buff, int left,int mid,int right){
		//int i = left,j = mid + 1,k = left;

	     if (((Comparable) array[mid]).compareTo(array[mid+1]) <= 0) {
             return;
         }
//
//		while(i <= mid && j <= right) {
//			if(array[i].compareTo(array[j]) < 0)
//				buff[k++] = array[i++];
//			else
//				buff[k++] = array[j++];
//		}
//
//		while(i <= mid)
//			buff[k++] = array[i++];
//		while(j <= right)
//			buff[k++] = array[j++];
//		for(i = left;i <= right; i++)
//			array[i] = buff[i];

		for (int i = left, p = left, q = mid+1; i <= right; i++) {
			if (q > right || p <= mid && ((Comparable) array[p]).compareTo(array[q]) <= 0) {
				buff[i] = array[p++];
			} else {
				buff[i] = array[q++];
			}
		}
//
//		for(int i = left;i <= right; i++)
//			array[i] = buff[i];

		System.arraycopy(buff, left, array, left, right-left+1);

	}




	private void reverse(Object[] array, Object[] buff, int left, int right) {

		for(int i = left,j = 0;i <= right;i++,j++)
			buff[i] = array[right-j];

//		for(int i = left;i <= right;i++){
//			array[i] = buff[i];
//		}

		System.arraycopy(buff, left, array, left, right-left+1);

	}


	public void mergeUpDown(Object[] array, Object[] buff, int left,int mid,int right){
		int i = left,j = right,k = left;

		while(i <= mid && j > mid) {
			if(((Comparable) array[i]).compareTo(array[j]) < 0)
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

	public void mergeDownUp(Object[] array, Object[] buff, int left,int mid,int right){
		int i = mid,j = mid+1,k = left;

		while(i >= left && j <= right) {
			if(((Comparable) array[i]).compareTo(array[j]) < 0)
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

	public void mergeDownDown(Object[] array, Object[] buff, int left,int mid,int right){
		int i = mid,j = right,k = left;

		while(i >= left && j > mid) {
			if(((Comparable) array[i]).compareTo(array[j]) < 0)
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
