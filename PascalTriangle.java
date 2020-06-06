
public class PascalTriangle {
	
	public static void main(String[] args) {
		
		
		for(int i =0;i<5;i++) {
			
			
			for(int j =0;j<=i;j++) {
				
				
				System.out.print(printPascal(i,j)+" ");
				
			}
				
				System.out.println();
			}
		
		//System.out.print(printPascal(5,3)+" ");
		
	}
			
			
		public static int printPascal(int i , int j) {
			 	
         int coefficient = fact(i)/(fact(j)*fact(i-j));
				
	     return coefficient;
		}
		
		public static int fact(int i) {
			
			int[] arr = new int[10];
			
			if(i==0||i==1) {
				return 1;
			}
			
			else if(arr[i]!=0) {
				return arr[i];
			}
			
			else 
				return arr[i]= i* fact(i-1);
			
			    		 
		}

		
		
}
