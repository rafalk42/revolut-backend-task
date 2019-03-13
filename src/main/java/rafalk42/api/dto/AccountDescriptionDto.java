package rafalk42.api.dto;

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
	
	public BigDecimal getInitialBalance()
	{
		return initialBalance;
	}
}
