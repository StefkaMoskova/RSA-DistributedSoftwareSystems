
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;

public class Determinant 
{
	private static int sizeOfMatrix = 0;
	
	public static BigDecimal det = new BigDecimal(0);
    public static boolean q = false;
    
    private static BigDecimal[][] generateMatrix(int size)
    {
    	BigDecimal[][] matrix = new BigDecimal[size][size];

    	for(int i = 0; i < size; i++)
    	{
    		for(int j = 0; j < size; j++)
    		{
    			matrix[i][j] =  new BigDecimal(ThreadLocalRandom.current().nextDouble(4096));
    		}
    	}
    	
		return matrix; 	
    }
    
    private static BigDecimal[][] readMatrix(String filename) throws FileNotFoundException
    {
    	File temp = new File(filename);
    	Scanner file = new Scanner(temp);
    	
    	sizeOfMatrix = file.nextInt();
    	
    	BigDecimal[][] returnMatrix = new BigDecimal[sizeOfMatrix][sizeOfMatrix];

    	for(int i = 0; i < sizeOfMatrix; i++)
    	{
    		for(int j = 0; j < sizeOfMatrix; j++)
    		{
    			returnMatrix[i][j] =  new BigDecimal(file.nextDouble());
    		}
    	}
    	
    	file.close();
		return returnMatrix;
    }
    
    public static void main(String[] args) 
    {
    	int threadsCount = 1;
    	
    	BigDecimal[][] A = null;
    	String outputFile = null;

        long begin = System.currentTimeMillis();

        CommandLineParser parser = new BasicParser();
        
    	Options options = new Options();
    	options.addOption("n","-size",true,"Size");
    	options.addOption("i","-input",true,"Input");
    	options.addOption("t","-threads", true,"Threads");
    	options.addOption("o","-output",true,"Output");
    	options.addOption("q","-quiet",false,"Mode");
    	
		try
		{
			CommandLine commandLine = parser.parse(options, args);
			
			if(commandLine.getOptionValue("n") != null)
			{
				sizeOfMatrix = Integer.parseInt(commandLine.getOptionValue("n"));
				A = generateMatrix(sizeOfMatrix);
				
			}
			
			else if(commandLine.getOptionValue("i") != null)
			{
				try 
				{
					A = readMatrix(commandLine.getOptionValue("i"));
				} 
				
				catch (FileNotFoundException e) 
				{
					e.printStackTrace();
				}
			}
			
			else
			{
				System.out.println("Please check your arguments!");
				System.exit(0);
			}
			
			if(commandLine.getOptionValue("t") != null)
			{
				threadsCount = Integer.parseInt(commandLine.getOptionValue("t"));
			}
			
			if(commandLine.getOptionValue("o") != null)
			{
				outputFile = commandLine.getOptionValue("o");
			}
			
			if(commandLine.hasOption("q"))
			{
				q = true;
			}
		}
		
		catch (org.apache.commons.cli.ParseException e) 
		{
			e.printStackTrace();	
		}
        
        int rowInterval = 0;
        int rowRange = 0;

		if(sizeOfMatrix > threadsCount)
		{
			if(threadsCount > 2)
			{
				rowInterval = sizeOfMatrix / (threadsCount - 1);
			}
			
			else
			{
				rowInterval = sizeOfMatrix / threadsCount;
			}
		}
		
		else
		{
			threadsCount = sizeOfMatrix;
			rowInterval = 1;
		}
		
        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);
        
        for(int i = 0; i < threadsCount; i++)
		{
			if( i != (threadsCount - 1))
			{
				executor.submit(new Task(rowRange, rowRange + rowInterval, A));
				rowRange += rowInterval;
			}
			
			else
			{
				executor.submit(new Task(rowRange, sizeOfMatrix, A));
			}
		}
        
        executor.shutdown();
        
        try 
        {
        	executor.awaitTermination(1,TimeUnit.DAYS);
        }
        
        catch (InterruptedException e) 
        {
        	e.printStackTrace();
        }
        
        long end = System.currentTimeMillis();
        long passed = end - begin;
        
        	if(outputFile != null)
        	{
            	try 
            	{
    				PrintWriter print = new PrintWriter(new File(outputFile));
    				print.println("Determinant: " + det);
    				print.close();
    			} 
            	
            	catch (FileNotFoundException e)
            	{
    				e.printStackTrace();
    			}
            }

        if (!q) 
        {
            System.out.println("Threads used in current run: " + threadsCount);
            System.out.println("Total execution time for current run (millis): " + passed);
        }

    }
}


