package rafalk42.api;

class TransferResultDto
{
	private final TransferDescriptionDto request;
	private final String result;
	
	TransferResultDto(TransferDescriptionDto request, String result)
	{
		this.request = request;
		this.result = result;
	}
}
