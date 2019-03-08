package rafalk42.rockefeller;

import rafalk42.domain.bank.BankAccount;


public class RockefellerBankAccount
		extends BankAccount
{
	private final String accountId;
	
	RockefellerBankAccount(String accountId)
	{
		this.accountId = accountId;
	}
	
	public String getAccountId()
	{
		return accountId;
	}
}
