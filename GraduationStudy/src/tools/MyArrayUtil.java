package tools;

import java.io.PrintWriter;
/*
 * ソート実験のに配列の操作のために使う道具
 */

public class MyArrayUtil extends ArrayUtil{

	public static synchronized <E extends Comparable> void print(Object[] a,int num){
			System.out.print("array["+num+"] : "); ((MyData)a[num]).print();
		System.out.println();
	}

	public static synchronized <E extends Comparable> void print(E[] a){

		for(E e : a)
			((MyData)e).print();
		System.out.println();
	}

	public static synchronized <E extends Comparable> void print2(E[] a){

		for(E e : a)
			System.out.print(e+" ");
		System.out.println();
	}

	public static synchronized <E extends Comparable> void print(int[] a){

		for(int e : a)
			System.out.printf("%2d",e);
		System.out.println();
	}

	public static synchronized <E extends Comparable> void print(Object[] a){
		for(Object e : a)
			((MyData)e).print();
		System.out.println();
	}

	public static synchronized <E extends Comparable> void print(E[] array,PrintWriter pw){
		for(E e : array)
			pw.print(" "+((MyData)e).getData());
		pw.println();
	}

	public static synchronized <E extends Comparable> void print(E[] array,PrintWriter pw,int num){

		pw.print("array["+num+ "] : "+((MyData)array[num]).getData());
		pw.println();
	}

	public static <E extends Comparable> void print(E[] a,int left,int right){
		for(int i = left;i <= right;i++)
			((MyData)a[i]).print();
		System.out.println();
	}

	public static<E extends Comparable> boolean checker(E[] a){
		int i;
		for( i = 0;i < a.length-1;i++){
			if(a[i].compareTo(a[i+1]) <= 0);
			else{
				return false;
			}

		}
		if(i == a.length-1){
			return true;

		}

		return false;
	}
}
