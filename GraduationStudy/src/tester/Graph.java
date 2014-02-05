package tester;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JFrame;

import mergeSort.MergeSort;
import mergeSort.ParallelMergeSort;
import newSort.NewSort2;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;


import quickMergeSort.QuickMergeSort2;
import quickSort.QuickSort;
import quickSort.QuickSort2;
import quickSort.ForkJoinQuickSort;
import quickSort.ParallelQuickSort;
import sort.Sort;
import tools.ArrayUtil;
import tools.MyArrayUtil;
import tools.MyInteger;

public class Graph extends JFrame{
	public static void main(String[] args){
		Graph frame = new Graph();

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(10, 10, 500, 500);
		frame.setTitle("sort");
		frame.setVisible(true);

	}

	Graph(){
		long[][] times = sortTimes();

		DefaultCategoryDataset data = new DefaultCategoryDataset();
		String[] series = {"qs", "qs2"," qsfj","qs6","ms","pms","Arrays","ns","qms"};
		String[] category = {"10000", "100000", "1000000"};

		for(int i = 0;i<3;i++){
			for(int j = 0;j < 9;j++){
				data.addValue(times[i][j],series[j],category[i]);
			}
		}


		JFreeChart chart =
				ChartFactory.createLineChart("sortTimes  coreSize : "+Runtime.getRuntime().availableProcessors(),
						"volumeOfData",
						"(ms)",
						data,
						PlotOrientation.VERTICAL,
						true,
						false,
						false);

		ChartPanel cpanel = new ChartPanel(chart);
		getContentPane().add(cpanel, BorderLayout.CENTER);

//	    File file = new File("./test1_1.jpeg");
//	    try {
//	      ChartUtilities.saveChartAsJPEG(file, chart, 400, 300);
//	    } catch (IOException e) {
//	      e.printStackTrace();
//	    }
	}

	public static long[][] sortTimes(){
		long[][] times = new long[3][9];
		int length = 1000;
		for(int i=0;i < 3;i++){
			times[i] = intSorting(length);
			length = length * 10;
		}

		return times;

	}
	public static long[] intSorting(int length){


		long[] times = new long[9];

		long[] start = new long[9];
		long[] stop = new long[9];

		MyInteger[] array = new MyInteger[length];
		int[] data = ArrayUtil.randomIntArray(array.length); //óêêî


		for(int i = 0;i < array.length;i++)
			array[i] = new MyInteger(data[i]);

		MyInteger[] array2 = Arrays.copyOf(array,array.length);
		MyInteger[] array4 = Arrays.copyOf(array,array.length);
		MyInteger[] array6 = Arrays.copyOf(array,array.length);
		MyInteger[] array7 = Arrays.copyOf(array,array.length);
		MyInteger[] array8 = Arrays.copyOf(array,array.length);
		MyInteger[] array9 = Arrays.copyOf(array,array.length);
		MyInteger[] array10 = Arrays.copyOf(array,array.length);
		MyInteger[] array11 = Arrays.copyOf(array,array.length);

		Sort<MyInteger> qs = new QuickSort<MyInteger>();
		Sort<MyInteger> qs2 = new QuickSort2<MyInteger>();
		Sort<MyInteger> qs6 = new ParallelQuickSort<MyInteger>();
		Sort<MyInteger> ms = new MergeSort<MyInteger>();
		//Sort<MyInteger> pms = new ParallelMergeSort<MyInteger>();//èCê≥
		Sort<MyInteger> qms = new QuickMergeSort2<MyInteger>();
		Sort<MyInteger> ns = new NewSort2<MyInteger>();
		ForkJoinQuickSort<MyInteger> qs5 = new ForkJoinQuickSort<MyInteger>();
		ParallelMergeSort<MyInteger> pms = new ParallelMergeSort<MyInteger>();

		System.out.println("-------QuickSort------");
		 start[0] = System.currentTimeMillis();
		qs.sort(array);
		stop[0] = System.currentTimeMillis();

		System.out.println("-------QuickSort2------");
		start[1] = System.currentTimeMillis();
		qs2.sort(array2);
		stop[1] = System.currentTimeMillis();


		System.out.println("-------ArraySort------");
		start[2] = System.currentTimeMillis();
		qs5.sort(array9);
		stop[2] = System.currentTimeMillis();


		System.out.println("-------QuickSort6------");
		start[3] = System.currentTimeMillis();
		qs6.sort(array6);
		stop[3] = System.currentTimeMillis();

		System.out.println("-------MergeSort------");
		start[4] = System.currentTimeMillis();
		ms.sort(array7);
		stop[4] = System.currentTimeMillis();

		System.out.println("-------NewSort------");
		start[5] = System.currentTimeMillis();
		pms.sort(array11);
		stop[5] = System.currentTimeMillis();


		//		MyArrayUtil.print(array8);

		start[6] = System.currentTimeMillis();
		 Arrays.sort(array4);
		stop[6] = System.currentTimeMillis();
		//MyArrayUtil.print(array9);

		//		MyArrayUtil.print(array8);

		start[7] = System.currentTimeMillis();
		ns.sort(array8);
		stop[7] = System.currentTimeMillis();
		//MyArrayUtil.print(array9);

		System.out.println("-------QuickMergeSort------");
		start[8] = System.currentTimeMillis();
		qms.sort(array10);
		stop[8] = System.currentTimeMillis();
		//		MyArrayUtil.print(array10);
		if(!MyArrayUtil.checker(array8)){
			System.out.println("False");
		}

		for(int i = 0;i < 9;i++)
			times[i] = stop[i] - start[i];

		return times;

	}
}
