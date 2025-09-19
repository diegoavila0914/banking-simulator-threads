/* Name: Diego Avila
 * Course: CNT 4714 Fall 2024
 * Assignment Title:
 * 		Project 2 - Synchronized/Cooperating Threads - A Banking Simulation
 * Due Date: September 22, 2024
 */

package development;

public interface TheBank 
{
	// add methods to interface as needed
	
	// deposit arguments: deposit amount, account number, thread name making the deposit
	public abstract void deposit(int depositAmount, String accountNum, String agentName);
	
	// withdraw arguments: withdraw amounts, account number, thread name making the withdraw
	public abstract void withdraw(int value, String accountNum, String agentName);
	
	// transfer arguments: transfer amount, from account, to account, thread name making the transfer
	public abstract void transfer(int transferAmount, BankAccount destinationAccount, String fromAcct, String toAcct, String agentName);
	
	// flagged transactions are logged independently into log file
	// flagged transactions arguments: transaction amount, thread name making the transaction, type of thread making the transaction
	// use "D" for depositor thread type, and "W" for withdrawal thread  type
	public abstract void flagged_transaction(String agentName, String transType, double amount, int transactionNum);
	
	// internal banking audit - examines balance only
	public abstract void internalAudit(String auditorThread, String account);
	
	// external banking audit - Treasury Department - examines balance only 
	public abstract void treasuryDepartmentAudit(String auditorThread, String account);

}
