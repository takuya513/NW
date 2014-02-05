package tools;

public class TestTools {

	public void p(String st,int i,String st2 ,int j){
		System.out.print("         "+st+" : "+i);
		System.out.println("      "+st2+" : "+j);
	}
	public void p(String st,int i){
		System.out.println("         "+st+" : "+i);
	}

	public void p(String st){
		System.out.print(""+st+" : ");
	}

	public void loopP(int loopNum,int data,String name){
		System.out.print(loopNum);
		for(int i = 0; i < loopNum;i++){
			System.out.print(" ");
		}
		System.out.println(" "+name+" : " +data);
	}

}
