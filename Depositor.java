/* Name: Diego Avila
 * Course: CNT 4714 Fall 2024
 * Assignment Title:
 * 		Project 2 - Synchronized/Cooperating Threads - A Banking Simulation
 * Due Date: September 22, 2024
 */

package development;

import java.util.Random;

public class Depositor implements Runnable 
{
	private static final int MAX_DEPOSIT = 600;
	private static final int MAXSLEEPTIME = 1700;
	private static Random depositAmount = new Random();
	private static Random sleepTime = new Random();
	private BankAccount jointAccount1;
	private BankAccount jointAccount2;
	
	private int accountNum;
	private int amount;
	private String tname;
	
	// constructor 
	public Depositor(BankAccount shared1, BankAccount shared2, String name) 
	{
		jointAccount1 = shared1;
		jointAccount2 = shared2;
		tname = name;
		
	}
	
	// Add money to the bank account
	public void run()
	{
		while(true)
		{
			try // sleep random time for simulation, select account, then add money 
			{
				
				// Randomly select an account (50-50 chance for each account number)
				accountNum = (Math.random() <= 0.5) ? 1 : 2;
				
				// Randomly generates a deposit amount from 1 to 600
				amount = depositAmount.nextInt(MAX_DEPOSIT) + 1;
				
				// Perform deposit operation
				if(accountNum == 1) {
					// make deposit in account 1
					jointAccount1.deposit(amount, "JA-1", tname);
	
				}
				else {
					//make deposit in account 2
					jointAccount2.deposit(amount, "JA-2", tname);
					
				}
				
				// Whether successful deposit or not - sleep agent for next attempt 
				//Thread.sleep(generator.nextInt(3000)); //sleep thread - was 3000 fixed time - see next line
				Thread.sleep(sleepTime.nextInt(MAXSLEEPTIME-1+1)+1); //sleep thread between 1 and MAXSLEEP
				//Thread.sleep(1);
				//Thread.yield(); 
				
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}

}
