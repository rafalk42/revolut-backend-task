package rafalk42.bank.domain;

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
	
	public TransferResult(Status status)
	{
		this.status = status;
	}
	
	public static TransferResult getSuccessful()
	{
		return new TransferResult(Status.SUCCESSFUL);
	}
	
	public static TransferResult getNotEnoughFunds()
	{
		return new TransferResult(Status.FAILED_NOT_ENOUGH_FUNDS);
	}
	
	public static TransferResult getInvalidAmount()
	{
		return new TransferResult(Status.FAILED_INVALID_AMOUNT);
	}
	
	public static TransferResult getNotAllowed()
	{
		return new TransferResult(Status.FAILED_NOT_ALLOWED);
	}
	
	public String getStatusString()
	{
		return status.name();
	}
	
	@Override
	public String toString()
	{
		return "TransferResult{" +
			   "status=" + status +
			   '}';
	}
}
