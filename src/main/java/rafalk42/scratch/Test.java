package rafalk42.scratch;

import rafalk42.dao.AccountDaoInMemory;
import rafalk42.domain.bank.Amount;
import rafalk42.domain.bank.Bank;
import rafalk42.domain.bank.BankAccount;
import rafalk42.domain.bank.BankAccountDescription;
import rafalk42.domain.dao.AccountDao;
import rafalk42.rockefeller.RockefellerBank;

import java.math.BigDecimal;
import java.util.Optional;


public class Test
{
	public static void main(String[] args)
	{
		try
		{
			AccountDao accountDao = new AccountDaoInMemory();
			Bank bank = new RockefellerBank(accountDao);
			
			BankAccountDescription accountDescription = new BankAccountDescription.Builder()
					.description("Something something")
					.initialAmount(Amount.fromBigDecimal(BigDecimal.valueOf(100)))
					.build();
			BankAccount bankAccount = bank.accountCreate(accountDescription);
			
//			Optional<BankAccount> account = bank.accountFindByStringId(id);
//			if (account.isPresent())
			{
				Amount balanceAmount = bank.getBalance(bankAccount);
				BigDecimal balance = balanceAmount.getAsBigDecimal();
				
				System.out.printf("Account balance: %.2f\n", balance.doubleValue());
			}
//			else
			{
//				System.out.println("Account not found");
			}
		}
		catch (Throwable ex)
		{
			ex.printStackTrace();
		}
	}
}
