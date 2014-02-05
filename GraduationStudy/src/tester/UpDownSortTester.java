package tester;

import java.util.Arrays;

import mergeSort.ForkJoinMergeSort;
import mergeSort.ForkJoinMergeSort2;



import quickSort.ForkJoinQuickSort;
import tools.MyArrayUtil;
import upDownSort.UpDownSort8_3;
import upDownSort.UpDownSort8_3_2;
import upDownSort.UpDownSort8_3_3;
import upDownSort.UpDownSort8_3_4;
import upDownSort.UpDownSort8_3_5;
import upDownSort.UpDownSort8_3_6;
import upDownSort.UpDownSort8_3_7;

public class UpDownSortTester {
	static final int SORTNUM = 20;
	static final int LENGTH = 10000000;
	static Integer[] array = new Integer[LENGTH];
	static long[] time = new long[SORTNUM];

	public Integer[] makingArray(int length,int didi){

		int i = 0;
		//didiÇ…ÇÊÇ¡ÇƒçÏÇÈÉ\Å[ÉgÇïœÇ¶ÇÈ
		while(i < length){
			for(int j = 0; j < length/didi;j++){
				array[i] = j;
				i++;
				if(i >= length){
					return array;
				}
			}



			for(int j = length/didi; j >= 0;j--){
				array[i] = j;
				i++;
				if(i >= length){
					return array;
				}
			}

		}
		return array;
	}



	public static void main(String args[]){
		UpDownSortTester ts = new UpDownSortTester();

		int k = 1;
		int j;
		//for(int k = 0 ; k< 10;k++){

		for(j = 1;j < LENGTH/2;j *= 2){
			ts.makingArray(LENGTH, j);

//			for(int i = 0;i < array.length;i++)
//				array[i] = new Integer(data[i]);


			System.out.println("didi : "+j);
//			fjqsSorting();
//			noNameSorting();

//			System.gc();
			fjmsSorting();


			//fjqsSorting();
			System.gc();
			 ArraysSorting();
//

			 System.gc();
			 noNameSorting();

			 System.gc();
			System.out.println();
			k *= 10;
		}
		//}

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
			fjms = new ForkJoinMergeSort2<Integer>();
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



		UpDownSort8_3_7<Integer> nns8_3;

		for(int i = 0;i < SORTNUM;i++){
			Integer[] arrayZ = array.clone();
			nns8_3 = new UpDownSort8_3_7<Integer>();
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

		System.out.println("noName7: "+(time2/SORTNUM));

	}




}
