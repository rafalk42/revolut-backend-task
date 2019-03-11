package rafalk42.bank.domain;

import java.math.BigDecimal;


public class BankAccountDescription
{
	private final String description;
	private final BigDecimal initialBalance;
	
	private BankAccountDescription(String description, BigDecimal initialBalance)
	{
		this.description = description;
		this.initialBalance = initialBalance;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public BigDecimal getInitialBalance()
	{
		return initialBalance;
	}
	
	public static class Builder
	{
		private String description;
		private BigDecimal initialAmount;
		
		public Builder()
		{
			description = null;
			initialAmount = null;
		}
		
		public Builder description(String description)
		{
			this.description = description;
			
			return this;
		}
		
		public Builder initialBalance(BigDecimal initialAmount)
		{
			this.initialAmount = initialAmount;
			
			return this;
		}
		
		public BankAccountDescription build()
		{
			return new BankAccountDescription(description, initialAmount);
		}
		
		public Builder initialBalance(double initialAmount)
		{
			this.initialAmount = BigDecimal.valueOf(initialAmount);
			
			return this;
		}
	}
	
	@Override
	public String toString()
	{
		return "BankAccountDescription{" +
			   "description='" + description + '\'' +
			   ", initialBalance=" + initialBalance +
			   '}';
	}
}
