package tools;

public class MyString implements Comparable,MyData{
	private String data;
	public MyString(String data){
		this.data = data;
	}

	public int getData() {
		//return data;
		return 1;
	}

	public void print(){
		System.out.println(" " + data);
	}

	public int compareTo(Object anotherData){
		int minLength;
		if(this.data.length() < ((MyString)anotherData).data.length())
			minLength = this.data.length();
		else
			minLength = ((MyString)anotherData).data.length();

		for(int i = 0;i < minLength;i++){
			if(this.data.charAt(i) < ((MyString)anotherData).data.charAt(i))
				return -1;
			if(this.data.charAt(i) > ((MyString)anotherData).data.charAt(i))
				return 1;
		}

		return 0;

	}
}
