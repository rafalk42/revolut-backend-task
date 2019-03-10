package rafalk42.bank.domain;

public class BankAccountInfo
{
	private final String description;
	private final Amount balance;
	
	public BankAccountInfo(String description, Amount balance)
	{
		this.description = description;
		this.balance = balance;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public Amount getBalance()
	{
		return balance;
	}
}
