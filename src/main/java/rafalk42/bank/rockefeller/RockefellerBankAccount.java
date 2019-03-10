package rafalk42.bank.rockefeller;

import rafalk42.bank.domain.BankAccount;

import java.util.Objects;


public class RockefellerBankAccount
		extends BankAccount
{
	private final String id;
	
	RockefellerBankAccount(String id)
	{
		if (id == null)
		{
			throw new IllegalArgumentException("ID cannot be null");
		}
		
		this.id = id;
	}
	
	@Override
	public String getId()
	{
		return id;
	}
	
	@Override
	public String toString()
	{
		return "RockefellerBankAccount{" +
				"id='" + id + '\'' +
				'}';
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
		RockefellerBankAccount that = (RockefellerBankAccount) o;
		return id.equals(that.id);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}
}
