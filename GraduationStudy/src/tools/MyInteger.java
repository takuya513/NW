package tools;

public class MyInteger implements Comparable,MyData{
	private int data;
	public MyInteger(int data){
		this.data = data;
	}
	public int getData() {
		return data;
	}
	public void setData(int data) {
		this.data = data;
	}
	public void print(){
		System.out.print(" " + data);
	}

	public synchronized int compareTo(Object anotherData){
		if(this.data < ((MyInteger)anotherData).data)
			return -1;
		else if(this.data == ((MyInteger)anotherData).data)
			return 0;
		else
			return 1;
	}


}