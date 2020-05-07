import java.math.BigDecimal;

public class Task implements Runnable 
{
	private int begin;
	private int end;
	
	private BigDecimal[][] A;

    public Task(int begin, int end, BigDecimal[][] A)
    {
		this.begin = begin;
		this.end = end;
		this.A = A;
	}
	
	@Override
	public void run()
	{
		long begining = 0;
		long ending = 0;
		long passed = 0;
		
		if(!Determinant.q)
		{
			System.out.println("Thread " + Thread.currentThread().getName() + " started.");
			begining = System.currentTimeMillis();
		}
		
		for(int i = begin; i < end; i++)
		{
			Determinant.det = Determinant.det.add(new BigDecimal(Math.pow(-1, i)).multiply(A[0][i]).multiply(det(calculateMinor(A, 0, i))));
		}
		
		if(!Determinant.q)
		{
			System.out.println("Thread " + Thread.currentThread().getName() + " stopped.");
			ending = System.currentTimeMillis();
			
			passed = ending - begining;
			System.out.println("Thread " + Thread.currentThread().getName() + " execution time was (millis): " + passed);
		}
	}
	
	   public BigDecimal det(BigDecimal[][] A) 
	    {
	        int size = A.length;
	        
	        if (size == 1) 
	        {
	            return A[0][0];
	        } 
	        
	        else 
	        {
	        	BigDecimal det = new BigDecimal(0);
	        	
	            for (int j = 0; j < size; j++) 
	            {
	            	det = det.add(new BigDecimal(Math.pow(-1, j)).multiply(A[0][j]).multiply(det(calculateMinor(A, 0, j))));
	            }
	            
	            return det;
	        }
	    }
	
    private BigDecimal[][] calculateMinor(final BigDecimal[][] A, final int i, final int j) 
    {
        int n = A.length;
        
        BigDecimal[][] minor = new BigDecimal[n-1][n-1];
       
        int rows = 0;
        int cols = 0;
        
        for (int p = 0; p < n; p++) 
        {
            BigDecimal[] currentRow = A[p];
            
            if (p != i)
            {
                for (int l = 0; l < currentRow.length; l++) 
                {
                    if (l != j) 
                    {
                        minor[rows][cols++] = currentRow[l];
                    }
                }
                
                rows++;
                cols = 0;
            }
        }
        
        return minor;
    }
}
