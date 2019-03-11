package rafalk42.bank.domain;

import java.math.BigDecimal;


public class TransferResult
{
	public enum Status
	{
		SUCCESSFUL,
		FAILED_INVALID_AMOUNT,
		FAILED_NOT_ENOUGH_FUNDS,
		FAILED_NOT_ALLOWED
	}
	
	private final Status status;
	private final BigDecimal actualAmount;
	
	private TransferResult(Status status, BigDecimal actualAmount)
	{
		this.status = status;
		this.actualAmount = actualAmount;
	}
	
	public static TransferResult getSuccessful(BigDecimal actualAmount)
	{
		return new TransferResult(Status.SUCCESSFUL, actualAmount);
	}
	
	public static TransferResult getNotEnoughFunds(BigDecimal actualAmount)
	{
		return new TransferResult(Status.FAILED_NOT_ENOUGH_FUNDS, actualAmount);
	}
	
	public static TransferResult getInvalidAmount(BigDecimal actualAmount)
	{
		return new TransferResult(Status.FAILED_INVALID_AMOUNT, actualAmount);
	}
	
	public static TransferResult getNotAllowed(BigDecimal actualAmount)
	{
		return new TransferResult(Status.FAILED_NOT_ALLOWED, actualAmount);
	}
	
	public String getStatusString()
	{
		return status.name();
	}
	
	public BigDecimal getActualAmount()
	{
		return actualAmount;
	}
	
	@Override
	public String toString()
	{
		return "TransferResult{" +
			   "status=" + status +
			   ", actualAmount=" + actualAmount +
			   '}';
	}
}
