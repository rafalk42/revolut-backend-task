package rafalk42.api.dto;

import java.math.BigDecimal;


public class TransferResultDto
{
	private final TransferDescriptionDto request;
	private final BigDecimal actualAmount;
	private final String result;
	
	public TransferResultDto(TransferDescriptionDto request, BigDecimal actualAmount, String result)
	{
		this.request = request;
		this.actualAmount = actualAmount;
		this.result = result;
	}
}
