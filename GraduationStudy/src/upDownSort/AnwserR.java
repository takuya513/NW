package upDownSort;

import java.util.ArrayList;
import java.util.Arrays;

import tester.Allpatern;

public class AnwserR<E extends Comparable>{
	static int[] kaisu = new int[10];
	public static void main(String args[]){
		int num = 4;
		int kai = 1;
		for(int i = 1; i <= num;i++)
			kai*=i;

		Allpatern ap = new Allpatern();
		AnwserR ar = new AnwserR();
		ar.makingAllPatternArray2(num);


		double ans = 0;
		for(int i = 1;i < 10;i++){
			System.out.println("i : "+i+"  kaisu : "+kaisu[i]);
			System.out.println("ans : "+(double)((double)kaisu[i]/(double)kai));
			ans += (double)(i * (double)((double)kaisu[i]/(double)kai));
		}
		System.out.println("kai : "+kai);
		System.out.println("ans : "+ans);
	}

	public void makingAllPatternArray(int NUM) {
		//ArrayList<Integer[]> list = new ArrayList<Integer[]>();
		AnwserR ar = new AnwserR();
		Integer[] array = new Integer[NUM];
		for(int i = 0;i < NUM;i++){
			array[i] = i+1;
		}

		int kai = 1;
		for(int i = 1; i <= NUM-1;i++)
			kai*=i;

		//System.out.println("kai : "+kai);
		int count = 1;
		int i = 0;
		int kk = 0;
		for(i = 0;i < NUM;i++){

			kk=0;
			int tmp = array[0];
			array[0] = array[i];
			array[i] = tmp;
			while(kk < kai){

				int tmp2 = array[count];
				array[count] = array[count + 1];
				array[count+1] = tmp2;

				for(int j = 0;j < NUM;j++)
					System.out.print(array[j]);
				int tasks = ar.createTasks(array);
				System.out.println("  taskNum : " +tasks);
				kaisu[tasks]++;
				//list.add(array.clone());

				//System.out.println();
				System.gc();
				count++;
				if(count == NUM-1)
					count=1;

				kk++;
			}
		}

	}
	public void makingAllPatternArray2(int NUM) {
		//ArrayList<Integer[]> list = new ArrayList<Integer[]>();
		AnwserR ar = new AnwserR();
		Integer[] array = new Integer[NUM];
		for(int i = 0;i < NUM;i++){
			array[i] = i+1;
		}

		int kai = 1;
		for(int i = 1; i <= NUM;i++)
			kai*=i;
		int i = 0;
		int pos = 0;
		while(i < kai){
			int tmp2 = array[pos];
			array[pos] = array[pos + 1];
			array[pos+1] = tmp2;
			
			
			for(int j = 0;j < NUM;j++)
				System.out.print(array[j]);
			int tasks = ar.createTasks(array);
			System.out.println("  taskNum : " +tasks);
			kaisu[tasks]++;
			pos++;
			if(pos == array.length-1)
				pos = 0;
			
			
			i++;
		}
	

	}

	public void makingAllPatternArray3(int NUM) {
		//ArrayList<Integer[]> list = new ArrayList<Integer[]>();
		AnwserR ar = new AnwserR();
		Integer[] array = new Integer[NUM];
		for(int i = 0;i < NUM;i++){
			array[i] = i+1;
		}
		Integer[] initArray = array.clone();


		int kai = 1;
		for(int i = 1; i <= NUM-1;i++)
			kai*=i;

		//System.out.println("kai : "+kai);
		int count = 1;
		//int i = 0;
		int kk = 0;
		//for(i = 0;i < NUM;i++){

		for(int i = array.length - 2;i >= 0;i--){
			//array = initArray.clone();
			int count2 = 0;
			int tmp = array[i];
			int pos = i;
			int first = pos;
			System.out.println("FIRST : "+first);
			do{
//				if(count2 != 0){
//					
//				}
//				count2++;
				
				int tmp2 = array[pos];
				array[pos] = array[pos + 1];
				array[pos+1] = tmp2;
				
				if(array[first] == tmp){
					tmp2 = array[pos + 1];
					array[pos + 1] = array[pos];
					array[pos] = tmp2;
					break;
				}

				pos++;
				if(pos == array.length-1)
					pos = first;



				for(int j = 0;j < NUM;j++)
					System.out.print(array[j]);
				int tasks = ar.createTasks(array);
				System.out.println("  taskNum : " +tasks);
				kaisu[tasks]++;



			}while(true);

		}




	}
	public int createTasks(Integer[] e){

		int rangeCount = 0;
		int arrayLength = e.length;
		int unitCount = 0;
		int pattern = 1;	//昇順昇順、降順降順などの区切った配列のマージパターンの指定用
		int left = 0;
		int mid = 0;
		int right = 0;


		while(right <= arrayLength - 1){


			if( e[right].compareTo(e[right+1]) <= 0){	//昇順なら

				pattern = pattern * (unitCount + 1);

				//long st = System.nanoTime();
				while(e[right].compareTo(e[right+1]) <= 0){  //どこまで昇順か調べる

					if(right+1 >= arrayLength-1){	//最後まで昇順であれば
						//long ed = System.nanoTime();
						//time += ed - st;
						if(unitCount == 0){
							//settingTask(array, buff, left,right+1,REST); //仕事をセットする
							return ++rangeCount;
						}else{
							right++;	//次に進める
							break;
						}
					}
					right++;  //次に進める
				}



			}else {//降順なら
				pattern = pattern * (unitCount + 2);
				while(e[right].compareTo(e[right+1]) > 0){//どこまで降順か調べる
					if(right+1 >= arrayLength-1){	//最後まで降順であれば
						if(unitCount == 0){		//二つのセットでなければ

							//settingTask(array,buff, left,right+1,ALLREVERSE);  //仕事をセットする
							return ++rangeCount;
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
					//					if(pattern == 1) //patternで判定する
					//						pattern = UPDOWN;
					//					else
					//						pattern = DOWNUP;
					//settingTask(array,buff, left,mid,right+1,pattern);  //MergeSortWorkerに仕事を投げる
					return ++rangeCount;
				}
			}else{	//二つのセットができたとき

				//settingTask(array, buff, left,mid,right,pattern);  //MergeSortWorkerに仕事を投げる



				if(right+1 == arrayLength-1){  //次の一つの要素が配列の終わりなら
					//settingTask(array, buff, right+1,right+1,REST);  //MergeSortWorkerに仕事を投げる
					return ++rangeCount;
				}
				pattern = 1; //初期化
				unitCount = 0;	//初期化
				left = right+1;
				rangeCount++;
			}
			right++;  //次に進める

		}//whlie文終了

		return rangeCount;
	}
}
