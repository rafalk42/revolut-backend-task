package rafalk42.api;

import java.math.BigDecimal;


class TransferResultDto
{
	private final TransferDescriptionDto request;
	private final BigDecimal actualAmount;
	private final String result;
	
	TransferResultDto(TransferDescriptionDto request, BigDecimal actualAmount, String result)
	{
		this.request = request;
		this.actualAmount = actualAmount;
		this.result = result;
	}
}
