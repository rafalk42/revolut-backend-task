package rafalk42.api.dto;

import java.math.BigDecimal;


public class DepositWithdrawResultDto
{
	private final DepositWithdrawDescriptionDto request;
	private final BigDecimal actualAmount;
	private final String result;
	
	public DepositWithdrawResultDto(DepositWithdrawDescriptionDto request, BigDecimal actualAmount, String result)
	{
		this.request = request;
		this.actualAmount = actualAmount;
		this.result = result;
	}
}
