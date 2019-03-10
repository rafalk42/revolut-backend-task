package rafalk42.api;

import java.math.BigDecimal;


class AccountBalanceDto
{
	private final BigDecimal balance;
	
	AccountBalanceDto(BigDecimal balance)
	{
		this.balance = balance;
	}
}
