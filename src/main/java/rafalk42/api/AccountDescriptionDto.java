package rafalk42.api;

import java.math.BigDecimal;


public class AccountDescriptionDto
{
	private final String description;
	private final BigDecimal initialBalance;
	
	public AccountDescriptionDto()
	{
		description = null;
		initialBalance = null;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	BigDecimal getInitialBalance()
	{
		return initialBalance;
	}
}
