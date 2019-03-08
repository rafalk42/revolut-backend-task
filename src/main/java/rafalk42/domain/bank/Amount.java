package rafalk42.domain.bank;

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
}
