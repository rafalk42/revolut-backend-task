package rafalk42.api;

import java.math.BigDecimal;


class AccountInfoDto
{
	private final String id;
	private final String description;
	private final BigDecimal balance;
	
	AccountInfoDto(String id, String description, BigDecimal balance)
	{
		this.id = id;
		this.description = description;
		this.balance = balance;
	}
}
