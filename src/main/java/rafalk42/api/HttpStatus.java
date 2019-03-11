package rafalk42.api;

public enum HttpStatus
{
	SUCCESS_OK(200),
	SUCCESS_CREATED(201),
	CLIENT_ERROR_BAD_REQUEST(400),
	CLIENT_ERROR_NOT_FOUND(404),
	CLIENT_ERROR_METHOD_NOT_ALLOWED(405),
	SERVER_ERROR_INTERNAL(500);
	private final int code;
	
	HttpStatus(int code)
	{
		this.code = code;
	}
	
	public int get()
	{
		return code;
	}
}
