/* Name: Diego Avila
 * Course: CNT 4714 Fall 2024
 * Assignment Title:
 * 		Project 2 - Synchronized/Cooperating Threads - A Banking Simulation
 * Due Date: September 22, 2024
 */

package development;

import java.util.Random;

public class Transfer implements Runnable
{
	private static final int MAX_TRANSFER = 550;
	private static final int MAXSLEEPTIME = 200;
	private static Random transferAmount = new Random();
	private static Random sleepTime = new Random();
	private BankAccount jointAccount1;
	private BankAccount jointAccount2;
	
	private int amount;
	private String tname;
	
	public Transfer(BankAccount shared1, BankAccount shared2, String name) {
		jointAccount1 = shared1;
		jointAccount2 = shared2;
		tname = name;
				
	}
	
	public void run() 
	{
		while(true)
		{
			try 
			{
				// Randomly select the source and destination accounts (ensure they are not the same)
                boolean useAccount1AsSource = (Math.random() <= 0.5);
                BankAccount sourceAccount = useAccount1AsSource ? jointAccount1 : jointAccount2;
                BankAccount destinationAccount = useAccount1AsSource ? jointAccount2 : jointAccount1;

                // Generate a random transfer amount
                int amount = transferAmount.nextInt(MAX_TRANSFER) + 1; // Random amount between 1 and MAX_TRANSFER

                // Perform the transfer operation
                sourceAccount.transfer(amount, destinationAccount,
                        sourceAccount == jointAccount1 ? "JA-1" : "JA-2",
                        destinationAccount == jointAccount1 ? "JA-1" : "JA-2", tname);

                // Sleep the thread for a random amount of time
                Thread.sleep(sleepTime.nextInt(MAXSLEEPTIME) + 1); // Random sleep time between 1 and MAXSLEEPTIME

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
		}
	}
}


