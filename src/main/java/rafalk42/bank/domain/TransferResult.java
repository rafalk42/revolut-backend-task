package rafalk42.bank.domain;

public class TransferResult
{
	public enum Status
	{
		SUCCESSFUL,
		INVALID_AMOUNT,
		NOT_ENOUGH_FUNDS
	}
	
	private final Status status;
	
	public TransferResult(Status status)
	{
		this.status = status;
	}
	
	public static TransferResult getOk()
	{
		return new TransferResult(Status.SUCCESSFUL);
	}
	
	public static TransferResult getNotEnoughFunds()
	{
		return new TransferResult(Status.NOT_ENOUGH_FUNDS);
	}
	
	public static TransferResult getInvalidAmount()
	{
		return new TransferResult(Status.INVALID_AMOUNT);
	}
}
