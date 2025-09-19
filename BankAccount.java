/* Name: Diego Avila
 * Course: CNT 4714 Fall 2024
 * Assignment Title:
 * 		Project 2 - Synchronized/Cooperating Threads - A Banking Simulation
 * Due Date: September 22, 2024
 */

package development;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount implements TheBank 
{
	// Lock to control mutually exclusive access to the bank account
	private ReentrantLock accessLock = new ReentrantLock();
	
	// condition variables as needed
	private Condition sufficientFunds = accessLock.newCondition();
	
	// variables for the bank account
	private int balance = 0;
	private static int transactionNum = 0;
	private static int transactionsSinceLastInternalAudit = 0;
	private static int transactionsSinceLastTreasuryAudit = 0;
	
	private static final int DEPOSIT_ALERT_LEVEL = 450;
	private static final int WITHDRAWAL_ALERT_LEVEL = 90;
	private static final int TRANSFER_ALERT_LEVEL = 500;
	
	public void lockAccount()
	{
		accessLock.lock();
	}
	
	public void unlockAccount()
	{
		if(accessLock.isHeldByCurrentThread()) 
		{
			accessLock.unlock();
		}
	}
	
	public int getTransactionsSinceLastInternalAuditnNum() 
	{
		return transactionsSinceLastInternalAudit;
	}
	
	public int getTransactionsSinceLastTreasuryAuditNum()
	{
		return transactionsSinceLastTreasuryAudit;
	}
	
	public int getBalance()
	{
		return balance;
	}
	
	public void resetTransactionsSinceLastInternalAudit()
	{
		transactionsSinceLastInternalAudit = 0;
	}
	
	public void resetTransactionsSinceLastTreasuryAudit()
	{
		transactionsSinceLastTreasuryAudit = 0;
	}

	
	// method used to log flagged transactions made against the bank account 
	public void flagged_transaction(String agentName, String transType, double amount, int transactionNum)
	{
		//Define the format for the date and time
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy 'at' HH:mm:ss z");
		String timestamp = dateFormat.format(new Date());
		
		// Define the format for the flagged transaction entry
		String logEntry = String.format("%s issued %s of $%.2f at: %s  Transaction Number: %d\n", agentName, transType, 
				amount, timestamp, transactionNum);
		
		// Log transaction that is flagged
		try(FileWriter writer = new FileWriter("transactions.csv", true)) {
			writer.append(logEntry);
			
		}
		
		catch(IOException e) {
			e.printStackTrace();
			
		}
	}
	
	
	// method used to make a deposit into the bank account(MORE PARAMETERS NEEDED IN PARENTHESES)
	public void deposit(int depositAmount, String accountNum, String agentName)
	{	
		// get the lock on the deposit
		accessLock.lock();
	
		
		try
		{
			// make deposit into account
			balance += depositAmount;
			transactionNum++;
			transactionsSinceLastInternalAudit++;
			transactionsSinceLastTreasuryAudit++;
			
			// Prints out the details of the transaction
			//System.out.printf("%s deposits $%3d into %s\t\t\t\t\t[+] %s balance is $%3d\t\t\t\t\t\t    %d \n\n", 
					//agentName, depositAmount, accountNum, accountNum, balance, transactionNum);
			
			System.out.printf("%s deposits $%3d into %-45s [+] %s balance is $%4d %49d\n\n", 
	                  agentName, depositAmount, accountNum, accountNum, balance, transactionNum);

			
			// handle transaction logging for flagged transaction
			if(depositAmount >= DEPOSIT_ALERT_LEVEL) {
				System.out.printf("\n\n\n* * * Flagged Transaction * * * %s Made A Deposit In Excess of $450.00 USD - See Flagged Transaction Log.\n\n", agentName);
				flagged_transaction(agentName, "deposit", depositAmount, transactionNum);
			}
			
			// signal all waiting threads that deposit has been made
			sufficientFunds.signalAll();
			
		}
		catch(Exception e) {
			System.out.println("An Exception was thrown making a deposit of funds.");
		}
		finally
		{
			//unlock the bank account
			accessLock.unlock();
			
		}
			
	}
	
	// method used to make a withdrawal from the bank account(MORE PARAMETERS NEEDED IN PARENTHESES)
	public void withdraw(int withdrawalAmount, String accountNum, String agentName)
	{
		// lock the bank account
		accessLock.lock();
		
		try
		{
			// attempt withdrawal
			// check for sufficient funds 
			while(balance < withdrawalAmount) 
			{
				System.out.printf("%-22s%-10s attempts to withdraw $%d from %s  (******) WITHDRAWAL BLOCKED - INSUFFICENT FUNDS!!! "
				+ "Balance only $%d\n\n", "", agentName, withdrawalAmount, accountNum, balance);
				sufficientFunds.await(); // waits for sufficient funds
				
			}
			// makes withdrawal
			balance -= withdrawalAmount;
			transactionNum++;
			transactionsSinceLastInternalAudit++;
			transactionsSinceLastTreasuryAudit++;
			
			// prints out the transaction details
			System.out.printf("%-22s%-10s withdraws $%2d from %-23s[-] %s balance is $%4d    %46d \n\n",
	                  "", agentName, withdrawalAmount, accountNum, accountNum, balance, transactionNum);
			
			// check for flagged transaction
			if(withdrawalAmount >= WITHDRAWAL_ALERT_LEVEL)
			{
				System.out.printf("\n\n\n* * * Flagged Transaction * * * %s Made A Withdrawal In Excess of $90.00 USD - See Flagged Transaction Log.\n\n", agentName);
				flagged_transaction(agentName, "withdrawal", withdrawalAmount, transactionNum);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		finally 
		{
			// unlock the bank account
			accessLock.unlock();
			
		}
		
	}

	
	// method used to make a transfer from one bank account to another bank account(MORE PARAMETERS NEEDED IN PARENTHESES)
	public void transfer(int transferAmount, BankAccount destinationAccount, String fromAcct, String toAcct, String agentName) 
	{
		// Determine lock order to prevent deadlocks
	    BankAccount firstLock, secondLock;
	    if (System.identityHashCode(this) < System.identityHashCode(destinationAccount)) {
	        firstLock = this;
	        secondLock = destinationAccount;
	    } else {
	        firstLock = destinationAccount;
	        secondLock = this;
	    }

	    // Lock the accounts in the determined order
	    firstLock.accessLock.lock();
	    try {
	        secondLock.accessLock.lock();
	        try {
	            // Check if there are sufficient funds in the source account for the transfer
	            if (balance < transferAmount) {
	            	System.out.printf(" %s transferring $%d from %s to %s (*****) TRANSFER ABORTED - INSUFFICIENT FUNDS!!!\n\n", agentName, transferAmount, 
	            			fromAcct, toAcct);
	                return; // Abort the transfer, no blocking
	            }

	            // Perform the transfer (atomic operation)
	            this.balance -= transferAmount; // Deduct from the source account
	            transactionNum++;
	            transactionsSinceLastInternalAudit++;
	            transactionsSinceLastTreasuryAudit++;

	            // Perform the deposit on the destination account directly
	            destinationAccount.balance += transferAmount; // Add to the destination account

	            // Print out the transaction details
	        	System.out.printf(" %s transferring $%3d from %s to %s - - %s balance is now $%3d%62d \n TRANSFER COMPLETE --> "
	        						+ "Account %s balance is now $%3d\n\n", agentName, transferAmount, fromAcct, toAcct, fromAcct, balance, transactionNum, toAcct, 
	        						destinationAccount.balance);

	            // Check for flagged transaction
	            if (transferAmount >= TRANSFER_ALERT_LEVEL) {	                
	            	System.out.printf("\n\n\n* * * Flagged Transaction * * * %s Made A Transfer In Excess of $500.00 USD - See Flagged Transaction Log.\n\n", agentName);
	            	flagged_transaction(agentName, "transfer", transferAmount, transactionNum);
	            }
	            

	            // Signal waiting threads for both accounts
	            this.sufficientFunds.signalAll();
	            destinationAccount.sufficientFunds.signalAll();

	        } finally {
	            // Unlock the second account
	            secondLock.accessLock.unlock();
	        }
	    } finally {
	        // Unlock the first account
	        firstLock.accessLock.unlock();
	    }
		
	}
	
	// method used to make an audit of the bank account(MORE PARAMETERS NEEDED IN PARENTHESES)
	public void internalAudit(String auditorThread, String account)
	{
		accessLock.lock();
		try
		{
			transactionsSinceLastInternalAudit = 0;
			
		}
		catch(Exception e)
		{
			System.out.println("An Exception was thrown getting the balance by an Internal Auditor");
		}
		finally {
			accessLock.unlock();
		}
		
	}
	
	//method used to make an audit of the bank account
	public void treasuryDepartmentAudit(String auditorThread, String account)
	{
		accessLock.lock();
		try
		{
			transactionsSinceLastTreasuryAudit = 0;
			
		}
		catch(Exception e)
		{
			System.out.println("An Exception was thrown getting the balance by an Internal Auditor");
			
		}
		finally
		{
			accessLock.unlock();
		}
		
	}

}
