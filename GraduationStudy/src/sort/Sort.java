package sort;

import java.io.File;

public interface Sort <E extends Comparable> {
	//void sort(int left,int right);
	void sort(E[] array);
}
