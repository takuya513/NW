package tools;
import java.util.Random;
import java.util.Scanner;

import org.apache.commons.lang3.RandomStringUtils;

/*
 * ソート実験の配列を生成、制御するクラス
 */
public class ArrayUtil {

	final static int DIDI = 2;
	public static Integer[] randomIntArray(int length){
		Integer[] a = new Integer[length];
		Scanner sc = new Scanner(System.in);
		//System.out.println("Input range of Intenger 0 ~ n: ");
		//System.out.print("n : ");
		//int n = sc.nextInt();
		int n = 10;
		for(int i = 0; i < a.length;i++)
			a[i] = generator.nextInt(n);

		return a;
	}

	public static Integer[] makeIntArray(int length){
		Integer[] a = new Integer[length];
		Scanner sc = new Scanner(System.in);
		//System.out.println("Input range of Intenger 0 ~ n: ");
		//System.out.print("n : ");
		//int n = sc.nextInt();
		for(int i = 0; i < a.length;){

//			for(int j = 0;j < a.length / DIDI;j++){
//
//				if(i >= a.length){
//					return a;
//				}
//				a[i++] = j;
//			}
//
//			for(int j = 0;j < a.length / DIDI;j++){
//
//				if(i >= a.length){
//					return a;
//				}
//				a[i++] = j;
//			}
//
//			for(int j = 0;j < a.length / DIDI;j++){
//
//				if(i >= a.length){
//					return a;
//				}
//				a[i++] = j;
//			}
			for(int k = a.length / DIDI;k >= 0;k--){
				if(i >= a.length){
					return a;
				}
				a[i++] = k;
			}
			for(int k = a.length / DIDI;k >= 0;k--){
				if(i >= a.length){
					return a;
				}
				a[i++] = k;
			}

//			for(int k = a.length / DIDI;k >= 0;k--){
//				if(i >= a.length){
//					return a;
//				}
//				a[i++] = k;
//			}
//
//

//
			for(int k = a.length / DIDI;k >= 0;k--){
				if(i >= a.length){
					return a;
				}
				a[i++] = k;
			}

		}
		return a;
	}



	public static double[] randomDoubleArray(int length){
		double[] a = new double[length];
		Scanner sc = new Scanner(System.in);
		//		System.out.println("Input range of Intenger 0 ~ n: ");
		//		System.out.print("n : ");
		//		long n = sc.nextLong();
		long n = 1000000000;
		for(int i = 0; i < a.length;i++)
			a[i] = n * generator.nextDouble();

		return a;
	}

	public static double[] makeDoubleArray(int length){
		double[] a = new double[length];
		Scanner sc = new Scanner(System.in);
		//System.out.println("Input range of Intenger 0 ~ n: ");
		//System.out.print("n : ");
		//int n = sc.nextInt();
		for(int i = 0; i < a.length;){

//			for(int j = 0;j < a.length / DIDI;j++){
//
//				if(i >= a.length){
//					return a;
//				}
//				a[i++] = j;
//			}
//
//			for(int j = 0;j < a.length / DIDI;j++){
//
//				if(i >= a.length){
//					return a;
//				}
//				a[i++] = j;
//			}
//
//			for(int j = 0;j < a.length / DIDI;j++){
//
//				if(i >= a.length){
//					return a;
//				}
//				a[i++] = j;
//			}
			for(int k = a.length / DIDI;k >= 0;k--){
				if(i >= a.length){
					return a;
				}
				a[i++] = k;
			}
			for(int k = a.length / DIDI;k >= 0;k--){
				if(i >= a.length){
					return a;
				}
				a[i++] = k;
			}

			for(int k = a.length / DIDI;k >= 0;k--){
				if(i >= a.length){
					return a;
				}
				a[i++] = k;
			}
//
//

//
			for(int k = a.length / DIDI;k >= 0;k--){
				if(i >= a.length){
					return a;
				}
				a[i++] = k;
			}

		}
		return a;
	}

	public static int[] myIntArray(int length){
		int a[] = new int[length];
		Scanner sc = new Scanner(System.in);
		for(int i = 0; i < a.length;i++){
			System.out.print("Input Integer Number : ");
			a[i] = sc.nextInt();
		}

		return a;
	}

	public static double[] myDoubleArray(int length){
		double[] a = new double[length];

		Scanner sc = new Scanner(System.in);
		for(int i = 0; i < a.length;i++){
			System.out.print("Input Integer Number : ");
			a[i] = sc.nextInt();
		}

		return a;
	}

	public static String[] randomStringArray(int length){
		String[] a = new String[length];
		Scanner sc = new Scanner(System.in);

		int n = 1000;
		for(int i = 0; i < a.length;i++)
			a[i] = RandomStringUtils.randomAlphabetic(n);

		return a;
	}

	private static Random generator = new Random();
}