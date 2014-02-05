package upDownSort;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * バージョン8_3_6のマージ部分で少なかったらinsertSort
 */


import tools.MyArrayUtil;
import upDownSort.NoNameSort9_2.MergeSortWorker;
/*
 * 最速、
 */
public class UpDownSort10_3<E extends Comparable> {
	private int arrayLength,taskNum;
	private int[] posPointer;//次のmiddle or right を覚えておく配列
	private volatile boolean[] isSorted;   //ソート完了部分と未完成部分の判定用
	private ExecutorService executor;
	private LinkedList<Future<Object>> futures;


	int time = 0; //テスト

	static final int ALLREVERSE = 1;  //指定の範囲の配列がすべて降順のとき
	static final int UPUP = 2;	//指定の範囲の配列が昇順、昇順の組合せのとき
	static final int UPDOWN = 3;	//指定の範囲の配列が昇順、降順の組合せのとき
	static final int DOWNUP = 4;	//指定の範囲の配列が降順、昇順の組合せのとき
	static final int DOWNDOWN = 6;	//指定の範囲の配列が降順、降順の組合せのとき
	static final int REST = 7;		////指定の範囲の配列が余りであり昇順のとき

	public UpDownSort10_3(){

	}

	public long sort(E[] array){

		time = 0;
		E[] buff = array.clone();
		long start = System.nanoTime();
		init(array);  //初期設定
		createTasks(array,buff);	//タスクを生成する



		for(Future f:futures){
			try {
				f.get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		long end = System.nanoTime();

		executor.shutdown();
		return end - start;
	}

	private void init(Object[] array) {
		arrayLength = array.length;
		isSorted = new boolean[arrayLength];
		posPointer = new int[arrayLength];
		taskNum = 0;


		Arrays.fill(posPointer, -1);  //posPointerの初期化

		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()-1);
		futures = new LinkedList<Future<Object>>();
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

				while(array[right].compareTo(array[right+1]) <= 0){  //どこまで昇順か調べる

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
		futures.add(executor.submit(Executors.callable(new MergeSortWorker(array, buff, left,mid,right,pattern))));
	}

	private void settingTask(E[] array,E[] buff, int left,int right,int pattern){
		updatePosPointer(left, right);
		futures.add(executor.submit(Executors.callable(new MergeSortWorker(array, buff, left,right,pattern))));
	}



	class MergeSortWorker implements Runnable{
		E[] array;
		E[] buff;
		private int left,right,mid,pattern;

		public MergeSortWorker(E[] array,E[] buff,int left,int mid,int right,int pattern){
			this.pattern = pattern;
			this.left = left;
			this.mid = mid;
			this.right = right;
			this.array = array;
			this.buff = buff;
		}


		public MergeSortWorker(E[] array,E[] buff,int left,int right,int pattern){
			this.left = left;
			this.right = right;
			this.pattern = pattern;
			this.array = array;
			this.buff = buff;
		}


		public void run(){

			firstMerge();  //最初の様々なパターンのマージ

			if(left == 0 && right == arrayLength - 1)
				return;

			expanseMerge();  //順次ソートしていく

		}

		private void expanseMerge() {
			do{

				if(right == arrayLength - 1){

					if(checkLeftSideIsSorted(left,right)){  //左のソートが終わっていれば
						//ソート範囲を拡大しmergeする
						mid = left - 1;
						left = posPointer[mid];
						merge(array,buff,left,mid,right);
					}else{
						return;//終了
					}


				}else if(left == 0){//最初の要素なら右だけ見る
					if(checkRightSideIsSorted(right,left)){	//右のソートが終わっていれば
						//ソート範囲を拡大しmergeする
						mid = right;
						right = posPointer[mid+1];
						merge(array,buff,left,mid,right);
					}else{ //右のソートが終わっていなければ終了
						return;//終了
					}
				}else{    //最初の要素でも最後の要素でもなけれ
					if(checkLeftSideIsSorted(left,right)){  //左のソートが終わっていれば
						//ソート範囲を拡大しmergeする
						mid = left - 1;
						left = posPointer[mid];
						merge(array,buff,left,mid,right);
					}else if(checkRightSideIsSorted(right,left)){	//右のソートが終わっていれば
						//ソート範囲を拡大しmergeする
						mid = right;
						right = posPointer[mid+1];
						merge(array,buff,left,mid,right);
					}else{ //どちらのソートも終わっていなければ
						return;
					}
				}

				if(left == 0 && right == arrayLength - 1){	//最後のソートであったら終了
					return;
				}

				updatePosPointer(left,right);//posPointerの更新
				changeIsSorted(left,right,true);
			}while(true);
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
				break;
			default :
				break;

			}

			changeIsSorted(left,right,true);
		}

		@SuppressWarnings("unchecked")
		public void merge(E[] array, E[] buff, int left,int mid,int right){

			if (array[mid].compareTo(array[mid+1]) <= 0) {
				return;
			}

			if(right -  left < 7){
				for(int i = left;i <= right;i++){
					for(int j = i; j > left && array[j - 1].compareTo(array[j]) > 0;j--)
						swap(array,j,j-1);
				}
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

						lastPos = i;
					}



					if(add >= 4){
						i = binarySearch(array,lastPos,endPos,array[j],endPos);
						System.arraycopy(array,lastPos,buff,k,i-lastPos);
						k+=i-lastPos;
					}else{
						while(array[i].compareTo(array[j]) < 0 && i <= endPos){
							buff[k++] = array[i++];
						}
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



		@SuppressWarnings("unchecked")
		public int binarySearch(E[] array,int left1,int right1,E key,int end){

			int left = left1;
			int right = right1;

			while(true){
				if(right - left == 0){
					if(array[left].compareTo(key) <= 0)
						return left+1;
					return left;
				}

				if(left > right)
					return left;

				int mid = (left + right) / 2;

				if(array[mid].compareTo(key) < 0){
					//return binarySearch(array,mid+1,right,key,end);
					left = mid+1;
				}else if(array[mid].compareTo(key) == 0){
					//return binarySearch(array,left+1,right,key,end);
					left = left+1;
				}else{
					//return binarySearch(array,left,mid-1,key,end);
					right = mid-1;
				}
			}
		}


		private void reverse(E[] array, E[] buff, int left, int right) {

			for(int i = left,j = right;i <= right;i++,j--)
				buff[i] = array[j];


			System.arraycopy(buff, left, array, left, right-left+1);

		}


		public void mergeUpDown(E[] array, E[] buff, int left,int mid,int right){

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

			System.arraycopy(buff, left, array, left, right-left+1);
		}

		public void mergeDownUp(E[] array, E[] buff, int left,int mid,int right){

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

			System.arraycopy(buff, left, array, left, right-left+1);
		}

		public void mergeDownDown(E[] array, E[] buff, int left,int mid,int right){

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

			System.arraycopy(buff, left, array, left, right-left+1);
		}
	}


	//posPointerを更新
	private void updatePosPointer(int pos1, int pos2) {
			posPointer[pos1] = pos2;//次のポジションの保存
			posPointer[pos2] = pos1; //前のポジションも保存
	}

	private  void changeIsSorted(int left,int right,boolean bool){
		synchronized(isSorted){
			isSorted[left] = bool;
			isSorted[right] = bool;
		}
	}



	private  boolean checkRightSideIsSorted(int right,int left){
		boolean bool = false;
		synchronized(isSorted){
			if((left != posPointer[right]) || (right != posPointer[left])){  //自分の領域がすでにマージされているかの確認
				return false;
			}

			if(isSorted[right] && isSorted[right+1]){	//右のソートが終わっていれば
				isSorted[left] = false;
				isSorted[right] = false;
				isSorted[posPointer[right+1]] = false;
				isSorted[right+1] = false;
				bool = true;
			}

		}
		return bool;
	}

	private  boolean checkLeftSideIsSorted(int left,int right){
		boolean bool = false;
		synchronized(isSorted){
			if((left != posPointer[right]) || (right != posPointer[left])){  //自分の領域がすでにマージされているかの確認
				return false;
			}

			if(isSorted[left] && isSorted[left-1]){	//左のソートが終わっていれば

				isSorted[left] = false;
				isSorted[right] = false;
				isSorted[posPointer[left-1]] = false;
				isSorted[left-1] = false;
				bool = true;
			}
		}
		return bool;
	}

	public void swap(E[] array,int i,int j){
		E temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}


}
