package tester;

import java.util.Arrays;
import java.util.Scanner;

import mergeSort.ForkJoinMergeSort;
import mergeSort.ForkJoinMergeSort2;
import mergeSort.MergeSort;
import mergeSort.ParallelMergeSort;
import newSort.NewSort2;
import newSort.NewSort3;
import newSort.NewSort4;
import newSort.NewSort5;
import quickMergeSort.QuickMergeSort2;
import quickMergeSort.QuickMergeSort3;
import quickMergeSort.QuickMergeSort4;
import quickMergeSort.QuickMergeSort5;
import quickMergeSort.QuickMergeSort5_2;
import quickMergeSort.QuickMergeSort6;
import quickSort.ForkJoinQuickSort;
import quickSort.ParallelQuickSort;
import quickSort.QuickSort;
import quickSort.QuickSort2;
import sort.Sort;
import tools.ArrayUtil;
import tools.MyArrayUtil;
import tools.MyDouble;
import tools.MyString;
import upDownSort.UpDownSort8;
import upDownSort.UpDownSort8_2;
import upDownSort.UpDownSort8_3;
import upDownSort.UpDownSort8_3_3;
import upDownSort.UpDownSort8_3_7;
import upDownSort.UpDownSort8_4;
import upDownSort.NoNameSort9;
import upDownSort.NoNameSort9_2;

public class QuickMergeSortTester {
	static final int SORTNUM = 20;
	static final int LENGTH = 100000000
;
	static long[] times = new long[SORTNUM];
	static Integer[] array = new Integer[LENGTH];
	//static Integer[] array = new Integer[LENGTH];
//	static MyDouble[] doubleArray = new MyDouble[LENGTH];
//	static MyString[] stringArray = new MyString[LENGTH];
	static int[] data;
	static String[] stringData;
	static double[] doubleData;



	public static void main(String args[]) throws InterruptedException {
//
//		for(int i = 100;i < 1000000000;i*= 10){
//			System.out.println("Length : "+i);
//			Integer[] array = new Integer[i];
			//integerInit(i,array);
//			integerSorting(i,array);
//		}

		System.out.println("LENGTH : "+LENGTH);
			integerInit(LENGTH);

		for(int threadNum = 1; threadNum <= 8;threadNum= threadNum + 1){
			System.out.println("threadNun : "+threadNum);
			qms6Sorting(array,threadNum);
			System.gc();

		}
		//doubleInit();
		//doubleSorting();
		//stringInit();
		//stringSorting();


		//noNameSorting();


	}

	private static void doubleSorting() {
//		qms5DoubleSorting();
//		qms5_2DoubleSorting();
		//qms6DoubleSorting();
		//ArraysDoubleSorting();
		//fjqsDoubleSorting();

	}





	private static void stringSorting() {
//		qms5StringSorting();
//		qms5_2StringSorting();
//		qms6StringSorting();
//		ArraysStringSorting();
//		fjqsStringSorting();
	}

	private static void integerSorting(int i, Integer[] array) {
		//qms5Sorting();
		ArraysSorting();


		qms6Sorting(array,Runtime.getRuntime().availableProcessors());
		for(Integer e:array){
			System.out.print(e);
		}
		System.out.println();
		ArraysSorting();
		fjqsSorting();

		System.out.println();
		//fjmsSorting();
		//noNameSorting();
	}

	private static void integerInit(int length) {
		array = ArrayUtil.randomIntArray(length); //????
		//stringData = MyArrayUtil.randomStringArray(LENGTH);

	}

	public static void stringInit(){
		stringData = MyArrayUtil.randomStringArray(LENGTH);


	}

	public static void qms5Sorting(Integer[] array){



		QuickMergeSort5<Integer> qms5;




		for(int i = 0;i < SORTNUM;i++){
			Integer[] arrayZ = array.clone();
			qms5 = new QuickMergeSort5<Integer>();
				times[i] = qms5.sort(arrayZ);

				if(!MyArrayUtil.checker(arrayZ)){
					System.out.println("["+i+"] " +" is False");
					//MyArrayUtil.print(arrays[i]);
				}
				System.gc();
		}



		System.out.println();
		System.out.println();


		long time = 0;
		for(int i = 1; i < SORTNUM;i++){
			time += times[i];
		}

		System.out.println("qms5   : "+(time/(SORTNUM-1)));

	}

	public static void qms5_2Sorting(Integer[] array){


		//int [] data = ArrayUtil.makeIntArray(array.length);

		Integer[][] arrayZ = new Integer[SORTNUM][array.length];
		for(int i = 0; i < SORTNUM;i++){
			arrayZ[i] = Arrays.copyOf(array,array.length);
		}

		QuickMergeSort5_2<Integer> qms5_2 = new QuickMergeSort5_2<Integer>();


		for(int i = 0;i < SORTNUM;i++){
			qms5_2 = new QuickMergeSort5_2<Integer>();
			times[i] = qms5_2.sort(arrayZ[i]);
		}


		System.out.println();
		System.out.println();
		for(int i = 0;i < SORTNUM;i++){
			if(!MyArrayUtil.checker(arrayZ[i])){
				System.out.println("["+i+"] " +" is False");
				//MyArrayUtil.print(arrays[i]);
			}
		}

		long time = 0;
		for(int i = 0; i < SORTNUM;i++){
			time += times[i];

		}

		System.out.println("qms5_2 : "+(time/SORTNUM));

	}


	public static void qms6Sorting(Integer[] array,int threadsNum){




		QuickMergeSort6<Integer> qms6;

		System.gc();
		for(int i = 0;i < SORTNUM;i++){
			Integer[] arrayZ = array.clone();
			qms6 = new QuickMergeSort6<Integer>();
			times[i] = qms6.sort(arrayZ,threadsNum);
			if(!MyArrayUtil.checker(arrayZ)){
				System.out.println("["+i+"] " +" is False");
				//MyArrayUtil.print(arrays[i]);
			}
			System.gc();
			//System.out.println("TIME : "+times[i]);
		}


		System.out.println();
		System.out.println();



		long time = 0;
		for(int i = 1; i < SORTNUM;i++){
			time += times[i];
			//System.out.println("TIME : "+times[i]);
		}

		System.out.println("threadsNum : "+threadsNum);
		System.out.println("qms6   : "+(time/SORTNUM));

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
			times[i] = fjqs.sort(arrayZ);
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
			time2 += times[i];
		}

		System.out.println("fjqs   : "+(time2/SORTNUM));

	}

	public static void fjmsSorting(){




		ForkJoinMergeSort2<Integer> fjms = new ForkJoinMergeSort2<Integer>();



		for(int i = 0;i < SORTNUM;i++){
			Integer[] arrayZ = array.clone();
			fjms = new ForkJoinMergeSort2<Integer>();
			System.gc();
			times[i] = fjms.sort(arrayZ);
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
			time2 += times[i];
		}

		System.out.println("fjms   : "+(time2/SORTNUM));

	}

	public static void noNameSorting(){

		//	int length = Integer.MAX_VALUE;



		UpDownSort8_3_7<Integer> nns8_3;

		for(int i = 0;i < SORTNUM;i++){
			Integer[] arrayZ = array.clone();
			nns8_3 = new UpDownSort8_3_7<Integer>();
			System.gc();
			times[i] = nns8_3.sort(arrayZ);

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
			time2 += times[i];
		}

		System.out.println("noName7: "+(time2/SORTNUM));

	}

//	public static void qms5StringSorting(){
//
//
//
//
//		MyString[][] arrayZ = new MyString[SORTNUM][array.length];
//		for(int i = 0; i < SORTNUM;i++){
//			arrayZ[i] = Arrays.copyOf(stringArray,stringArray.length);
//		}
//
//		QuickMergeSort5<MyString> qms5 = new QuickMergeSort5<MyString>();
//
//
//
//
//		for(int i = 0;i < SORTNUM;i++){
//			start[i] = System.nanoTime();;
//			qms5 = new QuickMergeSort5<MyString>();
//			qms5.sort(arrayZ[i]);
//			stop[i] = System.nanoTime();;
//		}
//
//
//
//		System.out.println();
//		System.out.println();
//		for(int i = 0;i < SORTNUM;i++){
//			if(!MyArrayUtil.checker(arrayZ[i])){
//				System.out.println("["+i+"] " +" is False");
//				//MyArrayUtil.print(arrays[i]);
//			}
//		}
//
//		long time = 0;
//		for(int i = 0; i < SORTNUM;i++){
//			time += (stop[i] - start[i]);
//		}
//
//		System.out.println("qms5   : "+(time/SORTNUM));
//
//	}
//
//	public static void qms5_2StringSorting(){
//
//
//		//int [] data = ArrayUtil.makeIntArray(array.length);
//		MyString[][] arrayZ = new MyString[SORTNUM][array.length];
//		for(int i = 0; i < SORTNUM;i++){
//			arrayZ[i] = Arrays.copyOf(stringArray,stringArray.length);
//		}
//
//		QuickMergeSort5_2<MyString> qms5_2 = new QuickMergeSort5_2<MyString>();
//
//
//
//
//		for(int i = 0;i < SORTNUM;i++){
//			start[i] = System.nanoTime();;
//			qms5_2 = new QuickMergeSort5_2<MyString>();
//			qms5_2.sort(arrayZ[i]);
//			stop[i] = System.nanoTime();;
//		}
//
//
//		System.out.println();
//		System.out.println();
//		for(int i = 0;i < SORTNUM;i++){
//			if(!MyArrayUtil.checker(arrayZ[i])){
//				System.out.println("["+i+"] " +" is False");
//				//MyArrayUtil.print(arrays[i]);
//			}
//		}
//
//		long time = 0;
//		for(int i = 0; i < SORTNUM;i++){
//			time += (stop[i] - start[i]);
//		}
//
//		System.out.println("qms5_2 : "+(time/SORTNUM));
//
//	}
//
//
//	public static void qms6StringSorting(){
//
//		//int [] data = ArrayUtil.makeIntArray(array.length);
//
//		MyString[][] arrayZ = new MyString[SORTNUM][array.length];
//		for(int i = 0; i < SORTNUM;i++){
//			arrayZ[i] = Arrays.copyOf(stringArray,stringArray.length);
//		}
//
//		QuickMergeSort6<MyString> qms6 = new QuickMergeSort6<MyString>();
//
//
//
//
//		for(int i = 0;i < SORTNUM;i++){
//			start[i] = System.nanoTime();;
//			qms6 = new QuickMergeSort6<MyString>();
//			qms6.sort(arrayZ[i]);
//			stop[i] = System.nanoTime();;
//		}
//
//
//		System.out.println();
//		System.out.println();
//		for(int i = 0;i < SORTNUM;i++){
//			if(!MyArrayUtil.checker(arrayZ[i])){
//				System.out.println("["+i+"] " +" is False");
//				//MyArrayUtil.print(arrays[i]);
//			}
//		}
//
//
//		long time = 0;
//		for(int i = 0; i < SORTNUM;i++){
//			time += (stop[i] - start[i]);
//		}
//
//		System.out.println("qms6   : "+(time/SORTNUM));
//
//	}
//
//	public static void ArraysStringSorting(){
//
//		//	int length = Integer.MAX_VALUE;
//
//		//int [] data = ArrayUtil.makeIntArray(array.length);
//
//		MyString[][] arrayZ = new MyString[SORTNUM][array.length];
//		for(int i = 0; i < SORTNUM;i++){
//			arrayZ[i] = Arrays.copyOf(stringArray,stringArray.length);
//		}
//
//
//
//		for(int i = 0;i < SORTNUM;i++){
//			start[i] = System.nanoTime();;
//			Arrays.sort(arrayZ[i]);
//			stop[i] = System.nanoTime();;
//		}
//
//
//
//
//		System.out.println();
//		System.out.println();
//		for(int i = 0;i < SORTNUM;i++){
//			if(!MyArrayUtil.checker(arrayZ[i])){
//				System.out.println("["+i+"] " +" is False");
//			}
//		}
//
//		long time = 0;
//		for(int i = 0; i < SORTNUM;i++){
//			time += (stop[i] - start[i]);
//		}
//
//		System.out.println("Arrays : "+(time/SORTNUM));
//
//	}
//
//	public static void fjqsStringSorting(){
//
//
//		//int [] data = ArrayUtil.makeIntArray(array.length);
//
//		MyString[][] arrayZ = new MyString[SORTNUM][array.length];
//		for(int i = 0; i < SORTNUM;i++){
//			arrayZ[i] = Arrays.copyOf(stringArray,stringArray.length);
//		}
//
//		ForkJoinQuickSort<MyString> fjqs = new ForkJoinQuickSort<MyString>();
//
//
//
//
//		for(int i = 0;i < SORTNUM;i++){
//			start[i] = System.nanoTime();;
//			fjqs = new ForkJoinQuickSort<MyString>();
//			fjqs.sort(arrayZ[i]);
//			stop[i] = System.nanoTime();;
//		}
//
//
//
//		for(int i = 0;i < SORTNUM;i++){
//			if(!MyArrayUtil.checker(arrayZ[i])){
//				System.out.println("["+i+"] " +" is False");
//				//MyArrayUtil.print(arrays[i]);
//			}
//		}
//		System.out.println();
//		System.out.println();
//		long time = 0;
//		for(int i = 0; i < SORTNUM;i++){
//			time += (stop[i] - start[i]);
//		}
//
//		System.out.println("fjqs   : "+(time/SORTNUM));
//
//	}
//
//	public static void noNameStringSorting(){
//
//		//	int length = Integer.MAX_VALUE;
//
//		MyString[][] arrayZ = new MyString[SORTNUM][array.length];
//		for(int i = 0; i < SORTNUM;i++){
//			arrayZ[i] = Arrays.copyOf(stringArray,stringArray.length);
//		}
//
//		UpDownSort8_3<MyString> nns8_3 = new UpDownSort8_3<MyString>();
//
//
//
//
//		for(int i = 0;i < SORTNUM;i++){
//			start[i] = System.nanoTime();;
//			nns8_3 = new UpDownSort8_3<MyString>();
//			nns8_3.sort(arrayZ[i]);
//			stop[i] = System.nanoTime();;
//		}
//
//
//
//		for(int i = 0;i < SORTNUM;i++){
//			if(!MyArrayUtil.checker(arrayZ[i])){
//				System.out.println("["+i+"] " +" is False");
//				//MyArrayUtil.print(arrays[i]);
//			}
//		}
//		System.out.println();
//		System.out.println();
//		long time = 0;
//		for(int i = 0; i < SORTNUM;i++){
//			time += (stop[i] - start[i]);
//		}
//
//		System.out.println("noName : "+(time/SORTNUM));
//
//	}
//
//	private static void fjqsDoubleSorting() {
//		//int [] data = ArrayUtil.makeIntArray(array.length);
//
//
//		MyDouble[][] arrayZ = new MyDouble[SORTNUM][array.length];
//		for(int i = 0; i < SORTNUM;i++){
//			arrayZ[i] = Arrays.copyOf(doubleArray,array.length);
//		}
//
//		ForkJoinQuickSort<MyDouble> fjqs = new ForkJoinQuickSort<MyDouble>();
//
//
//
//		for(int i = 0;i < SORTNUM;i++){
//			start[i] = System.nanoTime();;
//			fjqs = new ForkJoinQuickSort<MyDouble>();
//			fjqs.sort(arrayZ[i]);
//			stop[i] = System.nanoTime();;
//		}
//
//
//
//		for(int i = 0;i < SORTNUM;i++){
//			if(!MyArrayUtil.checker(arrayZ[i])){
//				System.out.println("["+i+"] " +" is False");
//				//MyArrayUtil.print(arrays[i]);
//			}
//		}
//		System.out.println();
//		System.out.println();
//		long time = 0;
//		for(int i = 0; i < SORTNUM;i++){
//			time += (stop[i] - start[i]);
//		}
//
//		System.out.println("fjqs   : "+(time/SORTNUM));
//
//	}
//
//	private static void ArraysDoubleSorting() {
//
//		MyDouble[][] arrayZ = new MyDouble[SORTNUM][array.length];
//		for(int i = 0; i < SORTNUM;i++){
//			arrayZ[i] = Arrays.copyOf(doubleArray,array.length);
//		}
//
//
//		for(int i = 0;i < SORTNUM;i++){
//			start[i] = System.nanoTime();;
//			Arrays.sort(arrayZ[i]);
//			stop[i] = System.nanoTime();;
//		}
//
//
//
//
//		System.out.println();
//		System.out.println();
//		for(int i = 0;i < SORTNUM;i++){
//			if(!MyArrayUtil.checker(arrayZ[i])){
//				System.out.println("["+i+"] " +" is False");
//			}
//		}
//
//		long time = 0;
//		for(int i = 0; i < SORTNUM;i++){
//			time += (stop[i] - start[i]);
//		}
//
//		System.out.println("Arrays : "+(time/SORTNUM));
//
//	}
//
//	private static void qms6DoubleSorting() {
//
//		MyDouble[][] arrayZ = new MyDouble[SORTNUM][array.length];
//		for(int i = 0; i < SORTNUM;i++){
//			arrayZ[i] = Arrays.copyOf(doubleArray,array.length);
//		}
//
//
//		QuickMergeSort6<MyDouble> qms6 = new QuickMergeSort6<MyDouble>();
//
//
//		for(int i = 0;i < SORTNUM;i++){
//			start[i] = System.nanoTime();;
//			qms6 = new QuickMergeSort6<MyDouble>();
//			qms6.sort(arrayZ[i]);
//			stop[i] = System.nanoTime();;
//		}
//
//
//		System.out.println();
//		System.out.println();
//		for(int i = 0;i < SORTNUM;i++){
//			if(!MyArrayUtil.checker(arrayZ[i])){
//				System.out.println("["+i+"] " +" is False");
//				//MyArrayUtil.print(arrays[i]);
//			}
//		}
//
//
//		long time = 0;
//		for(int i = 0; i < SORTNUM;i++){
//			time += (stop[i] - start[i]);
//		}
//
//		System.out.println("qms6   : "+(time/SORTNUM));
//
//	}
//
//	private static void qms5_2DoubleSorting() {
//		//int [] data = ArrayUtil.makeIntArray(array.length);
//
//
//
//		MyDouble[][] arrayZ = new MyDouble[SORTNUM][array.length];
//		for(int i = 0; i < SORTNUM;i++){
//			arrayZ[i] = Arrays.copyOf(doubleArray,array.length);
//		}
//
//		QuickMergeSort5_2<MyDouble> qms5_2 = new QuickMergeSort5_2<MyDouble>();
//
//
//		for(int i = 0;i < SORTNUM;i++){
//			start[i] = System.nanoTime();;
//			qms5_2 = new QuickMergeSort5_2<MyDouble>();
//			qms5_2.sort(arrayZ[i]);
//			stop[i] = System.nanoTime();;
//		}
//
//
//		System.out.println();
//		System.out.println();
//		for(int i = 0;i < SORTNUM;i++){
//			if(!MyArrayUtil.checker(arrayZ[i])){
//				System.out.println("["+i+"] " +" is False");
//				//MyArrayUtil.print(arrays[i]);
//			}
//		}
//
//		long time = 0;
//		for(int i = 0; i < SORTNUM;i++){
//			time += (stop[i] - start[i]);
//		}
//
//		System.out.println("qms5_2 : "+(time/SORTNUM));
//	}
//
//	private static void qms5DoubleSorting() {
//		MyDouble[][] arrayZ = new MyDouble[SORTNUM][array.length];
//		for(int i = 0; i < SORTNUM;i++){
//			arrayZ[i] = Arrays.copyOf(doubleArray,array.length);
//		}
//
//		QuickMergeSort5<MyDouble> qms5 = new QuickMergeSort5<MyDouble>();
//
//
//		for(int i = 0;i < SORTNUM;i++){
//			start[i] = System.nanoTime();;
//			qms5 = new QuickMergeSort5<MyDouble>();
//			qms5.sort(arrayZ[i]);
//			stop[i] = System.nanoTime();;
//		}
//
//
//
//		System.out.println();
//		System.out.println();
//		for(int i = 0;i < SORTNUM;i++){
//			if(!MyArrayUtil.checker(arrayZ[i])){
//				System.out.println("["+i+"] " +" is False");
//				//MyArrayUtil.print(arrays[i]);
//			}
//		}
//
//		long time = 0;
//		for(int i = 0; i < SORTNUM;i++){
//			time += (stop[i] - start[i]);
//		}
//
//		System.out.println("qms5   : "+(time/SORTNUM));
//
//	}

}
