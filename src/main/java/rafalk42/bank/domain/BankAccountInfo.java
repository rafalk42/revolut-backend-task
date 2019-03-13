package rafalk42.bank.domain;

import java.math.BigDecimal;


/**
 * Represents a collection of detailed information about a bank account.
 */
public class BankAccountInfo
{
	private final String description;
	private final BigDecimal balance;
	
	public BankAccountInfo(String description, BigDecimal balance)
	{
		this.description = description;
		this.balance = balance;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public BigDecimal getBalance()
	{
		return balance;
	}
	
	@Override
	public String toString()
	{
		return "BankAccountInfo{" +
			   "description='" + description + '\'' +
			   ", balance=" + balance +
			   '}';
	}
}
