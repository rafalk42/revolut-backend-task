package rafalk42.api.dto;

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
	
	public String getSourceAccountId()
	{
		return sourceAccountId;
	}
	
	public String getDestinationAccountId()
	{
		return destinationAccountId;
	}
	
	public BigDecimal getAmount()
	{
		return amount;
	}
}
