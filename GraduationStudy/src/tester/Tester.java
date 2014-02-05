package tester;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

import newSort.*;

import mergeSort.*;
import quickMergeSort.*;
import quickSort.*;
import sort.*;
import tools.*;
import upDownSort.*;

public class Tester {
	static Scanner sc;
	static int outCount = 0;
	static File file ;

	final static String[] sortNames = {"QuickSort","QuickSort2","ArraysSort","ParallelQuickSort",
		"ForkJoinQuickSort","MergeSort","ParallelMergeSort","NewSort2",
		"QuickMergeSort","QuickMergeSort3","QuickMergeSort4","QuickMergeSort5","NewSort3","NewSort4","NoNameSort8","NoNameSort9"
		,"QuickMergeSort6","QuickMergeSort5_2","NoNameSort9_2","NoNameSort8_2","NoNameSort8_3","NoNameSort8_4"};

	static final int SORTNUM = sortNames.length;
	static long[] start = new long[SORTNUM];
	static long[] stop = new long[SORTNUM];
	public static void main(String args[]) throws InterruptedException {
		sc = new Scanner(System.in);
		//file = new File("C:\\Users\\takuya\\Desktop\\chenLec\\chen_zemi\\GraduationStudy\\test.txt");
		sortChecer();
		//intSorting();
		//pinPointCheak();
		//doubleSorting();
		//stringSorting();
	}

	public static void sortChecer(){
		//int i = 24;

		boolean b = false;
		long start = System.currentTimeMillis();

		for(int j = 0;j < 2;j++){
			for(int i = 100;i <1000;i++)
				if(!intSortingCheck(i)){
					b = true;
					break;
				}
			if(b == true)
				break;
		}

		//intSortingCheck(i);
		long stop = System.currentTimeMillis();
		System.out.println("Finish");
		System.out.println("outCount : "+outCount);
		System.out.println("Time : "+(stop-start));

	}

	public static boolean intSortingCheck(int t){


		int length = t;

		MyInteger[] array = new MyInteger[length];
		//Integer[] data = ArrayUtil.makeIntArray(array.length); //óêêî

		Integer[] data = ArrayUtil.randomIntArray(array.length); //óêêî
		//int [] data = ArrayUtil.makeIntArray(array.length);


		for(int i = 0;i < array.length;i++)
			array[i] = new MyInteger(data[i]);



		MyInteger[] array10 = Arrays.copyOf(array,array.length);
		MyInteger[] array11 = Arrays.copyOf(array,array.length);
		MyInteger[] array12 = Arrays.copyOf(array,array.length);
		MyInteger[] array13 = Arrays.copyOf(array,array.length);
		MyInteger[] array14 = Arrays.copyOf(array,array.length);
		MyInteger[] array9 = Arrays.copyOf(array,array.length);

		Sort<MyInteger> qs6 = new ParallelQuickSort<MyInteger>();
		Sort<MyInteger> pms = new ParallelMergeSort<MyInteger>();//èCê≥
		Sort<MyInteger> qms = new QuickMergeSort<MyInteger>();
		Sort<MyInteger> qms_2 = new QuickMergeSort2<MyInteger>();
		Sort<MyInteger> qms3 = new QuickMergeSort3<MyInteger>();
		Sort<MyInteger> qms4 = new QuickMergeSort4<MyInteger>();
		QuickMergeSort5_2<MyInteger> qms5 = new QuickMergeSort5_2<MyInteger>();
		QuickMergeSort6<MyInteger> qms6 = new QuickMergeSort6<MyInteger>();
		QuickMergeSort6_2<MyInteger> qms6_2 = new QuickMergeSort6_2<MyInteger>();
		QuickMergeSort6_4<MyInteger> qms6_3 = new QuickMergeSort6_4<MyInteger>();
		Sort<MyInteger> ns = new NewSort<MyInteger>();
		Sort<MyInteger> ns2 = new NewSort2<MyInteger>();
		Sort<MyInteger> ns3 = new NewSort3<MyInteger>();
		Sort<MyInteger> ns4 = new NewSort4<MyInteger>();
		Sort<MyInteger> ns5 = new NewSort5<MyInteger>();
		Sort<MyInteger> ns6 = new NewSort6<MyInteger>();
		//NoNameSort<MyInteger> nns = new NoNameSort<MyInteger>();
		NoNameSort5<MyInteger> nns = new NoNameSort5<MyInteger>();
		NoNameSort6<MyInteger> nns6 = new NoNameSort6<MyInteger>();
		NoNameSort7<MyInteger> nns7 = new NoNameSort7<MyInteger>();
		UpDownSort8<MyInteger> nns8 = new UpDownSort8<MyInteger>();
		UpDownSort8_2<MyInteger> nns8_2 = new UpDownSort8_2<MyInteger>();
		UpDownSort8_3<MyInteger> nns8_3 = new UpDownSort8_3<MyInteger>();
		UpDownSort8_3_2<MyInteger> nns8_3_2 = new UpDownSort8_3_2<MyInteger>();
		UpDownSort8_3_6<MyInteger> nns8_3_6 = new UpDownSort8_3_6<MyInteger>();
		UpDownSort10_3<MyInteger> nns8_3_7 = new UpDownSort10_3<MyInteger>();
		UpDownSort8_4<MyInteger> nns8_4 = new UpDownSort8_4<MyInteger>();
		NoNameSort9<MyInteger> nns9 = new NoNameSort9<MyInteger>();
		NoNameSort9_2<MyInteger> nns9_2 = new NoNameSort9_2<MyInteger>();
		ForkJoinMergeSort2<MyInteger> fjms = new ForkJoinMergeSort2<MyInteger>();



		//		long start11 = System.currentTimeMillis();
		//		qms.sort(array10);
		//		long stop11 = System.currentTimeMillis();

		//		long start10 = System.currentTimeMillis();
		//qms_2.sort(array11);
		//		long stop10 = System.currentTimeMillis();



		//MyArrayUtil.print(array12);
		long start12 = System.currentTimeMillis();
		nns8_3_7.sort(array12);
		long stop12 = System.currentTimeMillis();
		//MyArrayUtil.print(array12);
		System.out.println("OKK");
		long start13 = System.currentTimeMillis();
		//nns.sort(array13);
		long stop13 = System.currentTimeMillis();

		long start14 = System.currentTimeMillis();
		//qms5.sort(array14);
		long stop14 = System.currentTimeMillis();

		//QuickSort5<MyInteger> qs5 = new QuickSort5<MyInteger>();
		//		qms3 = new QuickMergeSort3<MyInteger>();
		//		long start9 = System.currentTimeMillis();
		//		qms3.sort(array9);
		//		long stop9 = System.currentTimeMillis();



		//MyArrayUtil.print(array14);
		System.out.println("length          : "+length);
		if(!MyArrayUtil.checker(array12)){

			//System.out.println("OUT");
			//System.out.println("length          : "+length);
			MyArrayUtil.print(array);
			MyArrayUtil.print(array12);



			outCount++;

			return false;
		}else{
			//System.out.println("OK");
			//MyArrayUtil.print(array14);
		}
		System.out.println();
		System.out.println();
		//System.out.println("start : "+start13+"  stop : "+stop13);
		//System.out.println("start : "+start14+"  stop : "+stop14);
		System.out.println("ns6 time : "+(stop12 - start12));
		System.out.println("ns3 time : "+(stop13 - start13));
		System.out.println("ns4 time : "+(stop14 - start14));
		System.out.println();
		System.out.println();
		System.out.println();

		return true;
		//		System.out.println("Quick Merge Sort_2 time  :"+(stop10 - start10)); //èCê≥
		//		System.out.println("Quick Merge Sort time  :"+(stop11 - start11)); //èCê≥


	}

	public static void intSorting(){

		int length =10000000;  //äwçZÇÃÉpÉ\ÉRÉìóp
		//	int length = Integer.MAX_VALUE;

		MyInteger[] array = new MyInteger[length];
		Integer[] data = ArrayUtil.randomIntArray(array.length); //óêêî
		//int [] data = ArrayUtil.makeIntArray(array.length);



		for(int i = 0;i < array.length;i++)
			array[i] = new MyInteger(data[i]);

		MyInteger[][] arrays = new MyInteger[SORTNUM][array.length];
		for(int i = 0; i < SORTNUM;i++){
			arrays[i] = Arrays.copyOf(array,array.length);
		}


		Sort<MyInteger> qs = new QuickSort<MyInteger>();
		Sort<MyInteger> qs2 = new QuickSort2<MyInteger>();
		Sort<MyInteger> pqs = new ParallelQuickSort<MyInteger>();
		ForkJoinQuickSort<MyInteger> fjqs = new ForkJoinQuickSort<MyInteger>();
		Sort<MyInteger> ms = new MergeSort<MyInteger>();
		ParallelMergeSort<MyInteger> pms = new ParallelMergeSort<MyInteger>();
		Sort<MyInteger> ns = new NewSort2<MyInteger>();
		Sort<MyInteger> qms = new QuickMergeSort2<MyInteger>();
		Sort<MyInteger> qms3 = new QuickMergeSort3<MyInteger>();
		Sort<MyInteger> qms4 = new QuickMergeSort4<MyInteger>();
		QuickMergeSort5<MyInteger> qms5 = new QuickMergeSort5<MyInteger>();
		QuickMergeSort5_2<MyInteger> qms5_2 = new QuickMergeSort5_2<MyInteger>();
		QuickMergeSort6<MyInteger> qms6 = new QuickMergeSort6<MyInteger>();
		Sort<MyInteger> ns3 = new NewSort3<MyInteger>();
		Sort<MyInteger> ns4 = new NewSort4<MyInteger>();
		Sort<MyInteger> ns5 = new NewSort5<MyInteger>();
		UpDownSort8<MyInteger> nns8 = new UpDownSort8<MyInteger>();
		UpDownSort8_2<MyInteger> nns8_2 = new UpDownSort8_2<MyInteger>();
		UpDownSort8_3<MyInteger> nns8_3 = new UpDownSort8_3<MyInteger>();
		UpDownSort8_4<MyInteger> nns8_4 = new UpDownSort8_4<MyInteger>();
		NoNameSort9<MyInteger> nns9 = new NoNameSort9<MyInteger>();
		NoNameSort9_2<MyInteger> nns9_2 = new NoNameSort9_2<MyInteger>();



		//QuickSort
		System.out.println(sortNames[0]);
		start[0] = System.currentTimeMillis();
		//qs.sort(arrays[0]);
		stop[0] = System.currentTimeMillis();

		//QuickSort2
		System.out.println(sortNames[1]);
		start[1] = System.currentTimeMillis();
		//qs2.sort(arrays[1]);
		stop[1] = System.currentTimeMillis();

		//ArraysSort
		System.out.println(sortNames[2]);
		start[2] = System.nanoTime();
		Arrays.sort(arrays[2]);
		stop[2] = System.nanoTime();;

		//ParallelQuickSort
		System.out.println(sortNames[3]);
		start[3] = System.currentTimeMillis();
		//pqs.sort(arrays[3]);
		stop[3] = System.currentTimeMillis();

		//ForkJoinQuickSort
		System.out.println(sortNames[4]);
		start[4] = System.nanoTime();;
		fjqs.sort(arrays[4]);
		stop[4] = System.nanoTime();;
		//		MyArrayUtil.print(array7);


		//MergeSort
		System.out.println(sortNames[5]);
		start[5] = System.currentTimeMillis();
		//ms.sort(arrays[5]);
		stop[5] = System.currentTimeMillis();


		//ParallelMergeSort
		System.out.println(sortNames[6]);
		start[6] = System.currentTimeMillis();
		//pms.sort(arrays[6]);
		stop[6] = System.currentTimeMillis();

		//NewSort2
		System.out.println(sortNames[7]);
		start[7] = System.currentTimeMillis();
		//ns.sort(arrays[7]);
		stop[7] = System.currentTimeMillis();

		//QuickMergeSort
		System.out.println(sortNames[8]);
		start[8] = System.currentTimeMillis();
		//qms.sort(arrays[8]);
		stop[8] = System.currentTimeMillis();

		//QuickMergeSort3
		System.out.println(sortNames[9]);
		start[9] = System.currentTimeMillis();
		//qms5.sort(arrays[9]);
		stop[9] = System.currentTimeMillis();

		//QuickMergeSort4
		System.out.println(sortNames[10]);
		start[10] = System.currentTimeMillis();
		//qms4.sort(arrays[10]);
		stop[10] = System.currentTimeMillis();

		//QuickMergeSort5
		System.out.println(sortNames[11]);
		start[11] = System.nanoTime();;
		qms5.sort(arrays[11]);
		stop[11] = System.nanoTime();;

		//NewSort3
		System.out.println(sortNames[12]);
		start[12] = System.currentTimeMillis();
		//ns3.sort(arrays[12]);
		stop[12] = System.currentTimeMillis();

		//NewSort4
		System.out.println(sortNames[13]);
		start[13] = System.currentTimeMillis();
		//ns4.sort(arrays[13]);
		stop[13] = System.currentTimeMillis();

		//NoNameSort8
		System.out.println(sortNames[14]);
		start[14] = System.currentTimeMillis();
		//nns8.sort(arrays[14]);
		stop[14] = System.currentTimeMillis();

		//"NoNameSort9"
		System.out.println(sortNames[15]);
		start[15] = System.currentTimeMillis();
		//nns9.sort(arrays[15]);
		stop[15] = System.currentTimeMillis();

		//QuickMergeSort6
		System.out.println(sortNames[16]);
		start[16] = System.nanoTime();;
		//qms6.sort(arrays[16]);
		stop[16] = System.nanoTime();;

		//QuickMergeSort5_2
		System.out.println(sortNames[17]);
		start[17] = System.nanoTime();;
		qms5.sort(arrays[17]);
		stop[17] = System.nanoTime();;

		//NoNameSort9_2
		System.out.println(sortNames[18]);
		start[18] = System.currentTimeMillis();
		//nns9_2.sort(arrays[18]);
		stop[18] = System.currentTimeMillis();

		//NoNameSort8_2
		System.out.println(sortNames[19]);
		start[19] = System.currentTimeMillis();
		//nns8_2.sort(arrays[19]);
		stop[19] = System.currentTimeMillis();

		//NoNameSort8_3
		System.out.println(sortNames[20]);
		start[20] = System.currentTimeMillis();
		//nns8_3.sort(arrays[20]);
		stop[20] = System.currentTimeMillis();

		//NoNameSort8_4
		System.out.println(sortNames[21]);
		start[21] = System.currentTimeMillis();
		//nns8_4.sort(arrays[21]);
		stop[21] = System.currentTimeMillis();

		System.out.println();
		System.out.println();
		for(int i = 0;i < SORTNUM;i++){
			if(!MyArrayUtil.checker(arrays[i])){
				System.out.println(sortNames[i]+ " is False");
				//MyArrayUtil.print(arrays[i]);
			}
		}

		System.out.println();
		System.out.println();

		for(int i = 0; i < SORTNUM;i++){
			System.out.println(sortNames[i]+"  times : "+(stop[i] - start[i]));
		}


	}


	private static void doubleSorting() {


		int length  = 100000000;


		MyDouble[] array = new MyDouble[length];
		//double[] data2 = MyArrayUtil.randomDoubleArray(array.length);
		double[] data2 = MyArrayUtil.makeDoubleArray(array.length);

		for(int i = 0;i < array.length;i++)
			array[i] = new MyDouble(data2[i]);


		MyDouble[][] arrays = new MyDouble[SORTNUM][array.length];
		for(int i = 0; i < SORTNUM;i++){
			arrays[i] = Arrays.copyOf(array,array.length);
		}



		Sort<MyDouble> qs = new QuickSort<MyDouble>();
		Sort<MyDouble> qs2 = new QuickSort2<MyDouble>();
		Sort<MyDouble> pqs = new ParallelQuickSort<MyDouble>();
		ForkJoinQuickSort<MyDouble> fjqs = new ForkJoinQuickSort<MyDouble>();
		Sort<MyDouble> ms = new MergeSort<MyDouble>();
		ParallelMergeSort<MyDouble> pms = new ParallelMergeSort<MyDouble>();
		Sort<MyDouble> ns = new NewSort2<MyDouble>();
		Sort<MyDouble> qms = new QuickMergeSort2<MyDouble>();
		Sort<MyDouble> qms3 = new QuickMergeSort3<MyDouble>();
		Sort<MyDouble> qms4 = new QuickMergeSort4<MyDouble>();
		QuickMergeSort5<MyDouble> qms5 = new QuickMergeSort5<MyDouble>();
		QuickMergeSort5_2<MyDouble> qms5_2 = new QuickMergeSort5_2<MyDouble>();
		QuickMergeSort6<MyDouble> qms6 = new QuickMergeSort6<MyDouble>();
		Sort<MyDouble> ns3 = new NewSort3<MyDouble>();
		Sort<MyDouble> ns4 = new NewSort4<MyDouble>();
		Sort<MyDouble> ns5 = new NewSort5<MyDouble>();
		UpDownSort8<MyDouble> nns8 = new UpDownSort8<MyDouble>();
		UpDownSort8_2<MyDouble> nns8_2 = new UpDownSort8_2<MyDouble>();
		UpDownSort8_3<MyDouble> nns8_3 = new UpDownSort8_3<MyDouble>();
		NoNameSort9<MyDouble> nns9 = new NoNameSort9<MyDouble>();
		NoNameSort9_2<MyDouble> nns9_2 = new NoNameSort9_2<MyDouble>();



		//QuickSort
		System.out.println(sortNames[0]);
		start[0] = System.currentTimeMillis();
		//qs.sort(arrays[0]);
		stop[0] = System.currentTimeMillis();

		//QuickSort2
		System.out.println(sortNames[1]);
		start[1] = System.currentTimeMillis();
		//qs2.sort(arrays[1]);
		stop[1] = System.currentTimeMillis();

		//ArraysSort
		System.out.println(sortNames[2]);
		start[2] = System.currentTimeMillis();
		Arrays.sort(arrays[2]);
		stop[2] = System.currentTimeMillis();

		//ParallelQuickSort
		System.out.println(sortNames[3]);
		start[3] = System.currentTimeMillis();
		//pqs.sort(arrays[3]);
		stop[3] = System.currentTimeMillis();

		//ForkJoinQuickSort
		System.out.println(sortNames[4]);
		start[4] = System.currentTimeMillis();
		//fjqs.sort(arrays[4]);
		stop[4] = System.currentTimeMillis();
		//		MyArrayUtil.print(array7);


		//MergeSort
		System.out.println(sortNames[5]);
		start[5] = System.currentTimeMillis();
		//ms.sort(arrays[5]);
		stop[5] = System.currentTimeMillis();


		//ParallelMergeSort
		System.out.println(sortNames[6]);
		start[6] = System.currentTimeMillis();
		//pms.sort(arrays[6]);
		stop[6] = System.currentTimeMillis();

		//NewSort2
		System.out.println(sortNames[7]);
		start[7] = System.currentTimeMillis();
		//ns.sort(arrays[7]);
		stop[7] = System.currentTimeMillis();

		//QuickMergeSort
		System.out.println(sortNames[8]);
		start[8] = System.currentTimeMillis();
		//qms.sort(arrays[8]);
		stop[8] = System.currentTimeMillis();

		//QuickMergeSort3
		System.out.println(sortNames[9]);
		start[9] = System.currentTimeMillis();
		//qms5.sort(arrays[9]);
		stop[9] = System.currentTimeMillis();

		//QuickMergeSort4
		System.out.println(sortNames[10]);
		start[10] = System.currentTimeMillis();
		//qms4.sort(arrays[10]);
		stop[10] = System.currentTimeMillis();

		//QuickMergeSort5
		System.out.println(sortNames[11]);
		start[11] = System.currentTimeMillis();
		//qms5.sort(arrays[11]);
		stop[11] = System.currentTimeMillis();

		//NewSort3
		System.out.println(sortNames[12]);
		start[12] = System.currentTimeMillis();
		//ns3.sort(arrays[12]);
		stop[12] = System.currentTimeMillis();

		//NewSort4
		System.out.println(sortNames[13]);
		start[13] = System.currentTimeMillis();
		//ns4.sort(arrays[13]);
		stop[13] = System.currentTimeMillis();

		//NoNameSort8
		System.out.println(sortNames[14]);
		start[14] = System.currentTimeMillis();
		nns8.sort(arrays[14]);
		stop[14] = System.currentTimeMillis();

		//"NoNameSort9"
		System.out.println(sortNames[15]);
		start[15] = System.currentTimeMillis();
		//nns9.sort(arrays[15]);
		stop[15] = System.currentTimeMillis();

		//QuickMergeSort6
		System.out.println(sortNames[16]);
		start[16] = System.currentTimeMillis();
		//qms6.sort(arrays[16]);
		stop[16] = System.currentTimeMillis();

		//QuickMergeSort5_2
		System.out.println(sortNames[17]);
		start[17] = System.currentTimeMillis();
		//qms5_2.sort(arrays[17]);
		stop[17] = System.currentTimeMillis();

		//NoNameSort9_2
		System.out.println(sortNames[18]);
		start[18] = System.currentTimeMillis();
		//nns9_2.sort(arrays[18]);
		stop[18] = System.currentTimeMillis();

		//NoNameSort8_2
		System.out.println(sortNames[19]);
		start[19] = System.currentTimeMillis();
		nns8_2.sort(arrays[19]);
		stop[19] = System.currentTimeMillis();

		//NoNameSort8_3
		System.out.println(sortNames[20]);
		start[20] = System.currentTimeMillis();
		nns8_3.sort(arrays[20]);
		stop[20] = System.currentTimeMillis();


		System.out.println();
		System.out.println();
		for(int i = 0;i < SORTNUM;i++){
			if(!MyArrayUtil.checker(arrays[i])){
				System.out.println(sortNames[i]+ " is False");
				//MyArrayUtil.print(arrays[i]);
			}
		}

		System.out.println();
		System.out.println();

		for(int i = 0; i < SORTNUM;i++){
			System.out.println(sortNames[i]+"  times : "+(stop[i] - start[i]));
		}

	}





	public static void stringSorting(){


		//System.out.println("Input arrays length");
		//int length = sc.nextInt();
		int length =1000000;  //äwçZÇÃÉpÉ\ÉRÉìóp

		MyString[] array = new MyString[length];
		String[] data = MyArrayUtil.randomStringArray(array.length);
		for(int i = 0;i < array.length;i++)
			array[i] = new MyString(data[i]);

		for(int i = 0;i < array.length;i++)
			array[i] = new MyString(data[i]);

		MyString[][] arrays = new MyString[SORTNUM][array.length];
		for(int i = 0; i < SORTNUM;i++){
			arrays[i] = Arrays.copyOf(array,array.length);
		}


		Sort<MyString> qs = new QuickSort<MyString>();
		Sort<MyString> qs2 = new QuickSort2<MyString>();
		Sort<MyString> pqs = new ParallelQuickSort<MyString>();
		ForkJoinQuickSort<MyString> fjqs = new ForkJoinQuickSort<MyString>();
		Sort<MyString> ms = new MergeSort<MyString>();
		ParallelMergeSort<MyString> pms = new ParallelMergeSort<MyString>();
		Sort<MyString> ns = new NewSort2<MyString>();
		Sort<MyString> qms = new QuickMergeSort2<MyString>();
		Sort<MyString> qms3 = new QuickMergeSort3<MyString>();
		Sort<MyString> qms4 = new QuickMergeSort4<MyString>();
		QuickMergeSort5<MyString> qms5 = new QuickMergeSort5<MyString>();
		QuickMergeSort5_2<MyString> qms5_2 = new QuickMergeSort5_2<MyString>();
		QuickMergeSort6<MyString> qms6 = new QuickMergeSort6<MyString>();
		Sort<MyString> ns3 = new NewSort3<MyString>();
		Sort<MyString> ns4 = new NewSort4<MyString>();
		Sort<MyString> ns5 = new NewSort5<MyString>();
		UpDownSort8<MyString> nns8 = new UpDownSort8<MyString>();
		UpDownSort8_2<MyString> nns8_2 = new UpDownSort8_2<MyString>();
		UpDownSort8_3<MyString> nns8_3 = new UpDownSort8_3<MyString>();
		NoNameSort9<MyString> nns9 = new NoNameSort9<MyString>();
		NoNameSort9_2<MyString> nns9_2 = new NoNameSort9_2<MyString>();



		//QuickSort
		System.out.println(sortNames[0]);
		start[0] = System.currentTimeMillis();
		//qs.sort(arrays[0]);
		stop[0] = System.currentTimeMillis();

		//QuickSort2
		System.out.println(sortNames[1]);
		start[1] = System.currentTimeMillis();
		//qs2.sort(arrays[1]);
		stop[1] = System.currentTimeMillis();

		//ArraysSort
		System.out.println(sortNames[2]);
		start[2] = System.currentTimeMillis();
		Arrays.sort(arrays[2]);
		stop[2] = System.currentTimeMillis();

		//ParallelQuickSort
		System.out.println(sortNames[3]);
		start[3] = System.currentTimeMillis();
		//pqs.sort(arrays[3]);
		stop[3] = System.currentTimeMillis();

		//ForkJoinQuickSort
		System.out.println(sortNames[4]);
		start[4] = System.currentTimeMillis();
		//fjqs.sort(arrays[4]);
		stop[4] = System.currentTimeMillis();
		//		MyArrayUtil.print(array7);


		//MergeSort
		System.out.println(sortNames[5]);
		start[5] = System.currentTimeMillis();
		//ms.sort(arrays[5]);
		stop[5] = System.currentTimeMillis();


		//ParallelMergeSort
		System.out.println(sortNames[6]);
		start[6] = System.currentTimeMillis();
		//pms.sort(arrays[6]);
		stop[6] = System.currentTimeMillis();

		//NewSort2
		System.out.println(sortNames[7]);
		start[7] = System.currentTimeMillis();
		//ns.sort(arrays[7]);
		stop[7] = System.currentTimeMillis();

		//QuickMergeSort
		System.out.println(sortNames[8]);
		start[8] = System.currentTimeMillis();
		//qms.sort(arrays[8]);
		stop[8] = System.currentTimeMillis();

		//QuickMergeSort3
		System.out.println(sortNames[9]);
		start[9] = System.currentTimeMillis();
		//qms5.sort(arrays[9]);
		stop[9] = System.currentTimeMillis();

		//QuickMergeSort4
		System.out.println(sortNames[10]);
		start[10] = System.currentTimeMillis();
		//qms4.sort(arrays[10]);
		stop[10] = System.currentTimeMillis();

		//QuickMergeSort5
		System.out.println(sortNames[11]);
		start[11] = System.currentTimeMillis();
		//qms5.sort(arrays[11]);
		stop[11] = System.currentTimeMillis();

		//NewSort3
		System.out.println(sortNames[12]);
		start[12] = System.currentTimeMillis();
		//ns3.sort(arrays[12]);
		stop[12] = System.currentTimeMillis();

		//NewSort4
		System.out.println(sortNames[13]);
		start[13] = System.currentTimeMillis();
		//ns4.sort(arrays[13]);
		stop[13] = System.currentTimeMillis();

		//NoNameSort8
		System.out.println(sortNames[14]);
		start[14] = System.currentTimeMillis();
		nns8.sort(arrays[14]);
		stop[14] = System.currentTimeMillis();

		//"NoNameSort9"
		System.out.println(sortNames[15]);
		start[15] = System.currentTimeMillis();
		//nns9.sort(arrays[15]);
		stop[15] = System.currentTimeMillis();

		//QuickMergeSort6
		System.out.println(sortNames[16]);
		start[16] = System.currentTimeMillis();
		//qms6.sort(arrays[16]);
		stop[16] = System.currentTimeMillis();

		//QuickMergeSort5_2
		System.out.println(sortNames[17]);
		start[17] = System.currentTimeMillis();
		//qms5_2.sort(arrays[17]);
		stop[17] = System.currentTimeMillis();

		//NoNameSort9_2
		System.out.println(sortNames[18]);
		start[18] = System.currentTimeMillis();
		//nns9_2.sort(arrays[18]);
		stop[18] = System.currentTimeMillis();

		//NoNameSort8_2
		System.out.println(sortNames[19]);
		start[19] = System.currentTimeMillis();
		nns8_2.sort(arrays[19]);
		stop[19] = System.currentTimeMillis();

		//NoNameSort8_3
		System.out.println(sortNames[20]);
		start[20] = System.currentTimeMillis();
		nns8_3.sort(arrays[20]);
		stop[20] = System.currentTimeMillis();


		System.out.println();
		System.out.println();
		for(int i = 0;i < SORTNUM;i++){
			if(!MyArrayUtil.checker(arrays[i])){
				System.out.println(sortNames[i]+ " is False");
				//MyArrayUtil.print(arrays[i]);
			}
		}

		System.out.println();
		System.out.println();

		for(int i = 0; i < SORTNUM;i++){
			System.out.println(sortNames[i]+"  times : "+(stop[i] - start[i]));
		}
	}
}