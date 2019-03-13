package rafalk42.bank.domain;

import java.math.BigDecimal;


/**
 * Represents a result (status) of a operation, such as deposition, withdrawal or transfer.
 * Additionally an actual amount is provided in case requested amount was changed, e.g. due to rounding.
 */
public class OperationResult
{
	public enum Status
	{
		/**
		 * Operation completed successfully.
		 */
		SUCCESSFUL,
		/**
		 * Amount requested is not valid, e.g. negative or less then allowed value.
		 */
		FAILED_INVALID_AMOUNT,
		/**
		 * Source account's balance is less than requested amount.
		 */
		FAILED_NOT_ENOUGH_FUNDS,
		/**
		 * Requested operation is not allowed due to business restrictions.
		 */
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
