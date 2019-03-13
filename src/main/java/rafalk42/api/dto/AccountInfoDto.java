package rafalk42.api.dto;

import java.math.BigDecimal;


public class AccountInfoDto
{
	private final String id;
	private final String description;
	private final BigDecimal balance;
	
	public AccountInfoDto(String id, String description, BigDecimal balance)
	{
		this.id = id;
		this.description = description;
		this.balance = balance;
	}
}
