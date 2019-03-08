package rafalk42.domain.bank;

public class BankAccountDescription
{
	private final String description;
	private final Amount initialBalance;
	
	private BankAccountDescription(String description, Amount initialBalance)
	{
		this.description = description;
		this.initialBalance = initialBalance;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public Amount getInitialBalance()
	{
		return initialBalance;
	}
	
	public static class Builder
	{
		private String description;
		private Amount initialAmount;
		
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
		
		public Builder initialAmount(Amount initialAmount)
		{
			this.initialAmount = initialAmount;
			
			return this;
		}
		
		public BankAccountDescription build()
		{
			return new BankAccountDescription(description, initialAmount);
		}
	}
}
