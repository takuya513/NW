package tester;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import mergeSort.ForkJoinMergeSort;
import mergeSort.ForkJoinMergeSort2;



import quickMergeSort.QuickMergeSort6_2;
import quickMergeSort.QuickMergeSort6_4;
import quickSort.ForkJoinQuickSort;
import tools.MyArrayUtil;
import tools.MyInteger;
import upDownSort.UpDownSort10;
import upDownSort.UpDownSort10_2;
import upDownSort.UpDownSort10_2_2;
import upDownSort.UpDownSort10_3;
import upDownSort.UpDownSort8_3;
import upDownSort.UpDownSort8_3_10;
import upDownSort.UpDownSort8_3_11;
import upDownSort.UpDownSort8_3_2;
import upDownSort.UpDownSort8_3_3;
import upDownSort.UpDownSort8_3_4;
import upDownSort.UpDownSort8_3_5;
import upDownSort.UpDownSort8_3_6;
import upDownSort.UpDownSort8_3_7;
import upDownSort.UpDownSort8_3_8;
import upDownSort.UpDownSort8_3_9;
/*
 * 一つの数字づつ変えていく
 */
public class UpDownSortTester2 {
	static final int SORTNUM = 20;
	static final int LENGTH = 10000000;
	//static final int LENGTH =100000;
	int rlength = LENGTH;
	//static final int LENGTH = 20;
	static Integer[] array = new Integer[LENGTH];
	static long[] time = new long[SORTNUM];
	Random rd = new Random();
	HashMap<Integer,?> hm = new HashMap();
	static int count = 0;
	public boolean makingArray(){

		//randomに数値を選出
		int i;
		do{

			i = rd.nextInt(LENGTH);

		}while(hm.containsKey(i) || i == 0);
		//それがすでにソートしたものか確認
		//確認されていればもう一度ランダムの繰り返し

		swap(i-1,i);
		hm.put(i, null);
		hm.put(i-1, null);
		if(( rlength = rlength - 2) < 0)
			return false;

		return true;
		//randomで選出したiとi-1をswap
		//iを2文木に保存


	}

	public void makingArray(int length,int didi){

		int i = 1;
		//didiによって作るソートを変える
		while(i < length){
			for(int j = 0; j < length/didi;j++){
				array[i] = j;
				i++;
				if(i >= length){
					return ;
				}
			}



			for(int j = length/didi; j >= 0;j--){
				array[i] = j;
				i++;
				if(i >= length){
					return ;
				}
			}

		}
	}

	public void swap(int i,int j){
		int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}



	public static void main(String args[]){
		//case1();
		//case2();
		case1();
		System.out.println("COUN : "+count);
	}

	private static void case2() {
			UpDownSortTester2 ts = new UpDownSortTester2();
			ts.init();

			int i = 1;
			for(int j = 2;j < LENGTH/2;j *= 2){
				ts.makingArray(LENGTH, j);
//				System.out.println("j : "+j);
//				System.out.println("change :"+ (int)(((double)(j*2)/LENGTH)*100)+"%");
					System.out.println("didi : "+j);

					for(Integer e:array)
						System.out.print(" "+e);
	//
//					System.out.println();
					//MyArrayUtil.print(array);

					//			fjqsSorting();
					//			noNameSorting();


					ArraysSorting();
					System.gc();

					noNameSorting();
					System.gc();



					//qms6Sorting();
					//System.gc();
					//fjqsSorting();

					fjmsSorting();
					System.gc();

					noNameSorting2();
					System.gc();

					qms6Sorting();
					System.gc();
					//qms6Sorting();
					System.out.println();

						i += 1;


				}
			}


	private static void case1() {
		UpDownSortTester2 ts = new UpDownSortTester2();
		ts.init();

		int i = 11;
		for(int j = 0;j < LENGTH/2;j ++){
			if(j != 0){
			if(!ts.makingArray()){
				System.out.println("OUT");
				break;
			}
			}
//			System.out.println("j : "+j);
//			System.out.println("change :"+ (int)(((double)(j*2)/LENGTH)*100)+"%");
			if(i == (int)(((double)(j*2)/LENGTH)*100)){
				System.out.println("swap : "+j);
				System.out.println("change :"+ ((double)(j*2)/LENGTH)*100+"%");

//				for(Integer e:array)
//					System.out.print(" "+e);
//
//				System.out.println();
				//MyArrayUtil.print(array);

				//			fjqsSorting();
				//			noNameSorting();



				//noNameSorting();
//				qms6Sorting();
				System.gc();
				//fjqsSorting();
				noNameSorting();
				System.gc();
				ArraysSorting();
				System.gc();

				noNameSorting2();
				System.gc();

				fjmsSorting();

				System.gc();
				System.gc();


				//qms6Sorting();
				System.out.println();

					i += 1;


			}
		}
	}

	private void init() {
//		for(int i = 0;i < LENGTH;i++){
//			array[i] = i;
//		}

		for(int i = LENGTH-1,j = 0;i >= 0;i--,j++){
			array[j] = i;
	}
	}


	public static void ArraysSorting(){
		long[] start = new long[SORTNUM];
		long[] stop = new long[SORTNUM];
		//	int length = Integer.MAX_VALUE;

		//int [] data = ArrayUtil.makeIntArray(array.length);





		for(int i = 0;i < SORTNUM;i++){
			Integer[] arrayZ = array.clone();

			start[i] = System.nanoTime();;
			Arrays.sort(arrayZ);
			stop[i] = System.nanoTime();;
			//System.out.println("TIEM : "+(stop[i] - start[i]));
			if(!MyArrayUtil.checker(arrayZ)){
				System.out.println("["+i+"] " +" is False");
			}
			System.gc();

		}




		System.out.println();
		System.out.println();


		long time = 0;
		for(int i = 0; i < SORTNUM;i++){
			time += (stop[i] - start[i]);
		}

		System.out.println("Arrays : "+(time/SORTNUM));

	}

	public static void fjqsSorting(){


		//int [] data = ArrayUtil.makeIntArray(array.length);

		//Integer[][] arrayZ = new Integer[SORTNUM][array.length];
//		for(int i = 0; i < SORTNUM;i++){
//			arrayZ[i] = Arrays.copyOf(array,array.length);
//		}

		ForkJoinQuickSort<Integer> fjqs = new ForkJoinQuickSort<Integer>();



		for(int i = 0;i < SORTNUM;i++){
			Integer[] arrayZ = array.clone();

			fjqs = new ForkJoinQuickSort<Integer>();
			System.gc();
			time[i] = fjqs.sort(arrayZ);
			if(!MyArrayUtil.checker(arrayZ)){
				System.out.println("["+i+"] " +" is False");
				//MyArrayUtil.print(arrays[i]);
			}
			System.gc();
		}



//		for(int i = 0;i < SORTNUM;i++){
//
//			if(!MyArrayUtil.checker(arrayZ)){
//				System.out.println("["+i+"] " +" is False");
//				//MyArrayUtil.print(arrays[i]);
//			}
//		}
		System.out.println();
		System.out.println();
		long time2 = 0;
		for(int i = 0; i < SORTNUM;i++){
			time2 += time[i];
		}

		System.out.println("fjqs   : "+(time2/SORTNUM));

	}

	public static void fjmsSorting(){




		ForkJoinMergeSort2<Integer> fjms = new ForkJoinMergeSort2<Integer>();



		for(int i = 0;i < SORTNUM;i++){
			Integer[] arrayZ = array.clone();
//			fjms = new ForkJoinMergeSort2<Integer>();
			System.gc();
			time[i] = fjms.sort(arrayZ);
			//System.out.println("TIEM : "+time[i]);
			if(!MyArrayUtil.checker(arrayZ)){
				System.out.println("["+i+"] " +" is False");
				//MyArrayUtil.print(arrays[i]);
			}
			System.gc();
		}



		System.out.println();
		System.out.println();
		long time2 = 0;
		for(int i = 0; i < SORTNUM;i++){
			time2 += time[i];
		}

		System.out.println("fjms   : "+(time2/SORTNUM));

	}

	public static void noNameSorting(){

		//	int length = Integer.MAX_VALUE;



		UpDownSort10_3<Integer> nns8_3;

		for(int i = 0;i < SORTNUM;i++){
			//System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIIIII");
			Integer[] arrayZ = array.clone();

			//System.gc();
			nns8_3 = new UpDownSort10_3<Integer>();
			time[i] = nns8_3.sort(arrayZ);

			if(!MyArrayUtil.checker(arrayZ)){
				count++;
				System.out.println("["+i+"] " +" is False!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11fgagagagrgagagagfasgagasgsagfagfagfaggffgaagg");
//				System.out.println("["+i+"] " +" is False!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11fgagagagrgagagagfasgagasgsagfagfagfaggffgaagg");
//				System.out.println("["+i+"] " +" is False!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11fgagagagrgagagagfasgagasgsagfagfagfaggffgaagg");
//				System.out.println("["+i+"] " +" is False!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11fgagagagrgagagagfasgagasgsagfagfagfaggffgaagg");
//				System.out.println("["+i+"] " +" is False!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11fgagagagrgagagagfasgagasgsagfagfagfaggffgaagg");
//				System.out.println("["+i+"] " +" is False!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11fgagagagrgagagagfasgagasgsagfagfagfaggffgaagg");
//				System.out.println("["+i+"] " +" is False!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11fgagagagrgagagagfasgagasgsagfagfagfaggffgaagg");
//				System.out.println("["+i+"] " +" is False!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11fgagagagrgagagagfasgagasgsagfagfagfaggffgaagg");
//				System.out.println("["+i+"] " +" is False!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11fgagagagrgagagagfasgagasgsagfagfagfaggffgaagg");
//				System.out.println("["+i+"] " +" is False!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11fgagagagrgagagagfasgagasgsagfagfagfaggffgaagg");
				//MyArrayUtil.print2(arrayZ);
				System.exit(0);
			}
			System.gc();
			//System.out.println("TIEM : "+time[i]);
		}




		System.out.println();
		System.out.println();

		long time2 = 0;
		for(int i = 0; i < SORTNUM;i++){
			time2 += time[i];
		}

		System.out.println("noName10:"+(time2/SORTNUM));

	}



	public static void noNameSorting2(){

		//	int length = Integer.MAX_VALUE;



		UpDownSort8_3_11<Integer> nns8_3;

		for(int i = 0;i < SORTNUM;i++){
			Integer[] arrayZ = array.clone();
			nns8_3 = new UpDownSort8_3_11<Integer>();
			System.gc();
			time[i] = nns8_3.sort(arrayZ);

			if(!MyArrayUtil.checker(arrayZ)){
				System.out.println("["+i+"] " +" is False");
				//MyArrayUtil.print(arrays[i]);
			}
			System.gc();
			//System.out.println("TIEM : "+time[i]);
		}




		System.out.println();
		System.out.println();

		long time2 = 0;
		for(int i = 0; i < SORTNUM;i++){
			time2 += time[i];
		}

		System.out.println("noName11:"+(time2/SORTNUM));

	}


	public static void qms6Sorting(){




		QuickMergeSort6_4<Integer> fjms = new QuickMergeSort6_4<Integer>();



		for(int i = 0;i < SORTNUM;i++){
			Integer[] arrayZ = array.clone();
			fjms = new QuickMergeSort6_4<Integer>();
			System.gc();
			time[i] = fjms.sort(arrayZ);
			//System.out.println("TIEM : "+time[i]);
			if(!MyArrayUtil.checker(arrayZ)){
				System.out.println("["+i+"] " +" is False");
				//MyArrayUtil.print(arrays[i]);
			}
			System.gc();
		}



		System.out.println();
		System.out.println();
		long time2 = 0;
		for(int i = 0; i < SORTNUM;i++){
			time2 += time[i];
		}

		System.out.println("qms64  : "+(time2/SORTNUM));

	}



}
