package rafalk42.api;

import java.math.BigDecimal;


public class TransferDescriptionDto
{
	private final String sourceAccountId;
	private final String destinationAccountId;
	private final BigDecimal amount;
	
	public TransferDescriptionDto(String sourceAccountId, String destinationAccountId, BigDecimal amount)
	{
		this.sourceAccountId = sourceAccountId;
		this.destinationAccountId = destinationAccountId;
		this.amount = amount;
	}
	
	String getSourceAccountId()
	{
		return sourceAccountId;
	}
	
	String getDestinationAccountId()
	{
		return destinationAccountId;
	}
	
	BigDecimal getAmount()
	{
		return amount;
	}
}
