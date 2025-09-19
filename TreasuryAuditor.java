/* Name: Diego Avila
 * Course: CNT 4714 Fall 2024
 * Assignment Title:
 * 		Project 2 - Synchronized/Cooperating Threads - A Banking Simulation
 * Due Date: September 22, 2024
 */

package development;

import java.util.Random;

public class TreasuryAuditor implements Runnable {
	public static final int MAXSLEEPTIME = 5500;
	private static Random sleepTime = new Random();
	private BankAccount jointAccount1;
	private BankAccount jointAccount2;
	
	private String tname;
	private String firstAccountName;
	private String secondAccountName;
	
	//Constructor
	public TreasuryAuditor(BankAccount shared1, BankAccount shared2, String name)
	{
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
				// Lock both accounts for atomic audit
				BankAccount firstLock, secondLock;
				
				//Determines the lock order to prevent deadlocks when locking both accounts
				if(System.identityHashCode(jointAccount1) < System.identityHashCode(jointAccount2))
				{
					firstLock = jointAccount1; //If JA-1 has a smaller hash, lock it first
					secondLock = jointAccount2; //Lock JA-2 second
					firstAccountName = "JA-1";
					secondAccountName = "JA-2";
					
				} 
				else
				{
					firstLock = jointAccount2; //Otherwise, lock JA-2 first
					secondLock = jointAccount1; //Lock JA-1 second
					firstAccountName = "JA-2";
					secondAccountName = "JA-1";
				}
				
				//Lock the first account
				firstLock.lockAccount();
				
				try 
				{
					//Lock the second account after the first one
					secondLock.lockAccount();
					try
					{	
						// print headings 
						System.out.println("\n\n\n*******************************************************************************************************************************************\n\n\n");
						System.out.println("UNITED STATES DEPARTMENT OF THE TREASURY - Bank Audit Beginning...\n");
						
						// print # transactions since last audit line
						System.out.printf("\tThe total number of transactions since the last Treasury Department audit is: %d\n\n", firstLock.getTransactionsSinceLastTreasuryAuditNum());
						// run audit numbers and print results
						System.out.printf("\tTREASURY DEPT AUDITOR FINDS THE CURRENT ACCOUNT BALANCE FOR %s TO BE: $%d \n", firstAccountName, firstLock.getBalance());
						System.out.printf("\tTREASURY DEPT AUDITOR FINDS THE CURRENT ACCOUNT BALANCE FOR %s TO BE: $%d \n\n", secondAccountName, secondLock.getBalance());
						System.out.println("UNITED STATES DEPARTMENT OF THE TREASURY - Bank Audit Terminated....\n\n\n");
						System.out.println("*******************************************************************************************************************************************\n\n\n");
						
						firstLock.resetTransactionsSinceLastTreasuryAudit();
						secondLock.resetTransactionsSinceLastTreasuryAudit();
					}
					finally
					{
						// Unlock the second account after completing audit 
						secondLock.unlockAccount();
					}
					
				}
				finally
				{
					//Unlock the first account after completing audit 
					firstLock.unlockAccount();
				}
				
				Thread.sleep(sleepTime.nextInt(MAXSLEEPTIME)+1);
				
				
				
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	

}
