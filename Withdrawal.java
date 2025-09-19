/* Name: Diego Avila
 * Course: CNT 4714 Fall 2024
 * Assignment Title:
 * 		Project 2 - Synchronized/Cooperating Threads - A Banking Simulation
 * Due Date: September 22, 2024
 */

package development;

import java.util.Random;

public class Withdrawal implements Runnable 
{
	private static final int MAX_WITHDRAWAL = 99;
	private static final int MAXSLEEPTIME = 200;
	private static Random withdrawAmount = new Random();
	private static Random sleepTime = new Random();
	private BankAccount jointAccount1;
	private BankAccount jointAccount2;
	
	private int accountNum;
	private int amount;
	private String tname;
	
	// Constructor 
	public Withdrawal(BankAccount shared1, BankAccount shared2, String name) 
	{
		jointAccount1 = shared1;
		jointAccount2 = shared2;
		tname = name;
		
	}
	
	// Withdraw money from the bank account
	public void run() 
	{
		while(true)
		{
			try
			{
				// Randomly select an account (50-50 chance for each account number)
				accountNum = (Math.random() <= 0.5) ? 1 : 2;
				
				// Generates a random withdraw amount
				amount = withdrawAmount.nextInt(MAX_WITHDRAWAL) + 1;
				
				// Perform withdraw operation
				if(accountNum == 1) {
					// make withdraw into account 1
					jointAccount1.withdraw(amount, "JA-1", tname);
					
				}
				else {
					// make withdraw into account 2
					jointAccount2.withdraw(amount, "JA-2", tname);
					
				}
				//sleep thread between 1 and MAXSLEEP
				Thread.sleep(sleepTime.nextInt(MAXSLEEPTIME-1+1)+1);
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

}
