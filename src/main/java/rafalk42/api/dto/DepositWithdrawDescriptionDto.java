package rafalk42.api.dto;

import java.math.BigDecimal;


public class DepositWithdrawDescriptionDto
{
	private final BigDecimal amount;
	private final String title;
	
	public DepositWithdrawDescriptionDto()
	{
		amount = null;
		title = null;
	}
	
	public BigDecimal getAmount()
	{
		return amount;
	}
	
	public String getTitle()
	{
		return title;
	}
}
