package rafalk42.bank.domain;

import java.math.BigDecimal;


public class OperationResult
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
	
	private OperationResult(Status status, BigDecimal actualAmount)
	{
		this.status = status;
		this.actualAmount = actualAmount;
	}
	
	public static OperationResult getSuccessful(BigDecimal actualAmount)
	{
		return new OperationResult(Status.SUCCESSFUL, actualAmount);
	}
	
	public static OperationResult getNotEnoughFunds(BigDecimal actualAmount)
	{
		return new OperationResult(Status.FAILED_NOT_ENOUGH_FUNDS, actualAmount);
	}
	
	public static OperationResult getInvalidAmount(BigDecimal actualAmount)
	{
		return new OperationResult(Status.FAILED_INVALID_AMOUNT, actualAmount);
	}
	
	public static OperationResult getNotAllowed(BigDecimal actualAmount)
	{
		return new OperationResult(Status.FAILED_NOT_ALLOWED, actualAmount);
	}
	
	public Status getStatus()
	{
		return status;
	}
	
	public BigDecimal getActualAmount()
	{
		return actualAmount;
	}
	
	@Override
	public String toString()
	{
		return "OperationResult{" +
			   "status=" + status +
			   ", actualAmount=" + actualAmount +
			   '}';
	}
}
