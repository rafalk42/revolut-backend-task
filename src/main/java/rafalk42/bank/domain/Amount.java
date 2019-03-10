package rafalk42.bank.domain;

import java.math.BigDecimal;


public class Amount
{
	private final BigDecimal amount;
	
	Amount(BigDecimal amount)
	{
		this.amount = amount;
	}
	
	public BigDecimal getAsBigDecimal()
	{
		return amount;
	}
	
	public static Amount fromBigDecimal(BigDecimal amount)
	{
		return new Amount(amount);
	}
	
	public static Amount fromDouble(double amount)
	{
		return new Amount(BigDecimal.valueOf(amount));
	}
	
	@Override
	public String toString()
	{
		return "Amount{" +
			   "amount=" + amount +
			   '}';
	}
}
