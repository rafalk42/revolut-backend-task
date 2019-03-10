package rafalk42.scratch;

import rafalk42.bank.domain.*;
import rafalk42.bank.rockefeller.RockefellerBank;
import rafalk42.dao.AccountDao;
import rafalk42.dao.AccountDaoInMemory;

import java.math.BigDecimal;


public class DevTesting
{
	public static void main(String[] args)
	{
		try
		{
			AccountDao accountDao = new AccountDaoInMemory();
			Bank bank = new RockefellerBank(accountDao);
			
			BankAccount account1 = bank.accountCreate(getBAD("First account",
															 100.00));
			BankAccount account2 = bank.accountCreate(getBAD("Second account",
															 100.00));
			BankAccount account3 = bank.accountCreate(getBAD("Third account",
															 100.00));
			BankAccount account4 = bank.accountCreate(getBAD("Fourth account",
															 100.00));
			
			System.out.printf("Created accounts: %s, %s, %s\n",
							  account1,
							  account2,
							  account3);
			
			System.out.printf("Balances before:\n\t%s: %s\n\t%s: %s\n\t%s: %s\n\t%s: %s\n",
							  account1, bank.accountGetBalance(account1),
							  account2, bank.accountGetBalance(account2),
							  account3, bank.accountGetBalance(account3),
							  account4, bank.accountGetBalance(account4));
			
			TransferResult result = transfer(bank, account4, account1, 1000.00);
			
			System.out.println(result);

//			for (int i = 0; i < 1000; i++)
//			{
//				List<Thread> threads = new ArrayList<>();
////			threads.add(new Thread(() -> transfer(bank, account4, account1, 10.00)));
////			threads.add(new Thread(() -> transfer(bank, account4, account2, 10.00)));
////			threads.add(new Thread(() -> transfer(bank, account4, account3, 10.00)));
//				threads.add(new Thread(() -> transfer(bank, account1, account2, 10.00)));
//				threads.add(new Thread(() -> transfer(bank, account2, account3, 10.00)));
//				threads.add(new Thread(() -> transfer(bank, account3, account1, 10.00)));
//
//				threads.forEach(Thread::start);
//				threads.forEach(thread ->
//								{
//									try
//									{
//										thread.join();
//									}
//									catch (InterruptedException ex)
//									{
//										ex.printStackTrace();
//									}
//								});
//			}
			
			System.out.printf("Balances after:\n\t%s: %s\n\t%s: %s\n\t%s: %s\n\t%s: %s\n",
							  account1, bank.accountGetBalance(account1),
							  account2, bank.accountGetBalance(account2),
							  account3, bank.accountGetBalance(account3),
							  account4, bank.accountGetBalance(account4));
		}
		catch (Throwable ex)
		{
			ex.printStackTrace();
		}
	}
	
	private static TransferResult transfer(Bank bank, BankAccount source, BankAccount destination, double amount)
	{
		try
		{
			return bank.transferAmount(source, destination, Amount.fromDouble(amount));
		}
		catch (BankInternalError | BankAccountNotFound ex)
		{
			ex.printStackTrace();
		}
		
		return null;
	}
	
	private static BankAccountDescription getBAD(String description, double initialBalance)
	{
		return new BankAccountDescription.Builder()
				.description(description)
				.initialBalance(Amount.fromBigDecimal(BigDecimal.valueOf(initialBalance)))
				.build();
	}
}
