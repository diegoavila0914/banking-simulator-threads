/* Name: Diego Avila
 * Course: CNT 4714 Fall 2024
 * Assignment Title:
 * 		Project 2 - Synchronized/Cooperating Threads - A Banking Simulation
 * Due Date: September 22, 2024
 */

package development;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BankingSimulator 
{
	// Fall 2024 - 10 withdraw threads, 5 depositor agents, 2 transfer threads, 1 internal auditor thread, and 1 treasury department auditor thread
	public static final int MAX_AGENTS = 20; 
	public static final int MAX_ACCOUNTS = 2;
	

	public static void main(String[] args) {
		//Redirect console to output text file
		try {
			//Create the file and redirect output to it
			PrintStream fileOut = new PrintStream(new File("simulationOutput.txt"));
			//System.setOut(fileOut);
			//Create a TeePrintStream that outputs to both the console and text file
			TeePrintStream teeStream = new TeePrintStream(fileOut, System.out);
			System.setOut(teeStream);
			
			// thread pool - size 20
			ExecutorService application = Executors.newFixedThreadPool(MAX_AGENTS); 
			// define the joint accounts
			BankAccount jointAccount1 = new BankAccount(); 
			BankAccount jointAccount2 = new BankAccount(); 
		
			try {
				// headings for the simulation run
				System.out.println("\u001B[32m* * *  SIMULATION BEGINS...");
				System.out.println();
				// Start threads - random order
				System.out.printf("%-33s %-46s %-54s %-25s\n", 
					    "Deposit Agents", "Withdrawal Agents", "Balances", "Transaction Number");
				System.out.printf("%-33s %-41s %-54s %-25s\n", 
					    "--------------", "-----------------", "--------------------", "--------------------------");
			
				// Current MAXSLEEP times are:    Withdrawals: 200  Depositors: 1700  Transfers: 2500  Internal Audit: 4500 Treasury Audit: 5500
				//
				// start 5 depositor agents to run
				for(int i = 1; i <= 5; i++)
				{
					// Constructs Depositor agents and executes deposit transactions
					Depositor depositor = new Depositor(jointAccount1, jointAccount2, "Agent DT" + i);
					application.execute(depositor);
				}
			
				// start 10 withdraw agents to run
				for(int i = 1; i <= 10; i++)
				{
					// Constructs Withdrawal agents and executes withdraw transactions
					Withdrawal withdrawal = new Withdrawal(jointAccount1, jointAccount2, "Agent WT" + i);
					application.execute(withdrawal);
				
				}
			
				// start 2 transfer agents to run
				for(int i = 1; i <= 2; i++)
				{
					// Constructs Transfer agents and executes transfer transactions
					Transfer transfer = new Transfer(jointAccount1, jointAccount2, "TRANSFER --> Agent TR" + i);
					application.execute(transfer);
				
				}
			
				// Start 1 internal audit agent to run
				InternalAuditor internalAuditor = new InternalAuditor(jointAccount1, jointAccount2, "*");
				application.execute(internalAuditor);
			
				// Start 1 treasury department audit agent to run
				TreasuryAuditor treasuryAuditor = new TreasuryAuditor(jointAccount1, jointAccount2, "*");
				application.execute(treasuryAuditor);
	
				//Allows the simulation to run for 20 seconds
				Thread.sleep(30000);
			
			}
		
			catch(Exception e) {
				e.printStackTrace();
			}
			finally {
				application.shutdown(); 
			}
		
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	
	}

}
