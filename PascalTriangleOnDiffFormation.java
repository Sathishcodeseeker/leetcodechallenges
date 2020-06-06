
public class PascalTriangleOnDiffFormation {
	
	public static void main(String[] args) {
		
		
		for(int i =1;i<=4;i++) {
			
			for(int k =1 ;k<=4-i;k++) {
				System.out.print(" ");
			}
					
			for(int j =1;j<=i;j++) {

				System.out.print(printPascal(i-1,j-1)+" ");
				
			}
				
				System.out.println();
			}
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
