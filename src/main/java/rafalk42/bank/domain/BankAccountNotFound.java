package rafalk42.bank.domain;

public class BankAccountNotFound
		extends Throwable
{
	private final String accountId;
	
	public BankAccountNotFound(String accountId)
	{
		this.accountId = accountId;
	}
	
	public String getAccountId()
	{
		return accountId;
	}
}
