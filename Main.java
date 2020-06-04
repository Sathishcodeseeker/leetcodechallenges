/******************************************************************************

                            Online Java Compiler.
                Code, Compile, Run and Debug java program online.
Write your code in this editor and press "Run" button to execute it.

*******************************************************************************/

import java.util.*;

public class Main
{
	public static void main(String[] args) {
		List<List<Integer>> mainList = new ArrayList<>(); 
		
		List<Integer> list1 = new ArrayList<>();
		list1.add(20);
		list1.add(60);
		
		List<Integer> list2 = new ArrayList<>();
		list2.add(10);
		list2.add(50);
		
		List<Integer> list3 = new ArrayList<>();
		list3.add(30);
		list3.add(190);
		
		List<Integer> list4 = new ArrayList<>();
		list4.add(30);
		list4.add(300);
		
		mainList.add(list1);
		mainList.add(list2);
		mainList.add(list3);
		mainList.add(list4);
		
		Collections.sort(mainList,(a,b) -> ((a.get(0)-a.get(1)) - (b.get(0)-b.get(1))));
		
	
		int sum = 0;
		
		int N = mainList.size();
		
		for(int i = 0;i<N/2;i++){
		    
		    sum += mainList.get(i).get(0);
		    
		}
		
		for(int j= N/2 ; j< N;j++){
		    
		     sum += mainList.get(j).get(1);
		}
		
		System.out.println(sum);
	}
}
