package rafalk42.dao;

import java.math.BigDecimal;
import java.util.Objects;


public class AccountInfo
{
	private final String id;
	private final String description;
	private final BigDecimal balance;
	
	public AccountInfo(String id, String description, BigDecimal balance)
	{
		if (id == null)
		{
			throw new IllegalArgumentException("ID cannot be null");
		}
		
		if (balance == null)
		{
			throw new IllegalArgumentException("Balance cannot be null");
		}
		
		this.id = id;
		this.description = description;
		this.balance = balance;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public BigDecimal getBalance()
	{
		return balance;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}
		AccountInfo that = (AccountInfo) o;
		return id.equals(that.id) &&
			   Objects.equals(description, that.description) &&
			   balance.equals(that.balance);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(id, description, balance);
	}
}
