package tester;

import java.util.ArrayList;

public class Allpatern {
	//static int NUM = 5;
	public static void main(String[] args){
		Allpatern pp = new Allpatern();
		pp.makingAllPatternArray(5);

	}
	public ArrayList<Integer[]> makingAllPatternArray(int NUM) {
		ArrayList<Integer[]> list = new ArrayList<Integer[]>();
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

//				for(int j = 0;j < NUM;j++)
//					System.out.print(array[j]);


				list.add(array.clone());

				//System.out.println();
				System.gc();
			count++;
			if(count == NUM-1)
				count=1;

			kk++;
		}
	}

		return list;
	}

}
