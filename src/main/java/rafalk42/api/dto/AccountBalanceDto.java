package rafalk42.api.dto;

import java.math.BigDecimal;


public class AccountBalanceDto
{
	private final BigDecimal balance;
	
	public AccountBalanceDto(BigDecimal balance)
	{
		this.balance = balance;
	}
}
