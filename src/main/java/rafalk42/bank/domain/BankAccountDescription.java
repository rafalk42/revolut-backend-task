package rafalk42.bank.domain;

import java.math.BigDecimal;


/**
 * Representation of all the information required to open an account.
 */
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
	
	/**
	 * Simple builder for creating and validating account description.
	 */
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
		
		/**
		 * Validate provided information and create an instance of BankAccountDescription.
		 * @return a new bank account description
		 */
		public BankAccountDescription build()
		{
			if (description == null)
			{
				throw new IllegalArgumentException("Description is mandatory");
			}
			
			if (initialAmount == null)
			{
				throw new IllegalArgumentException("Initial amount is mandatory");
			}
			
			if (initialAmount.compareTo(BigDecimal.ZERO) < 0)
			{
				throw new IllegalArgumentException("Initial amount cannot be negative");
			}
			
			return new BankAccountDescription(description, initialAmount);
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
