package rafalk42.api;

import spark.Request;
import spark.Response;
import spark.Spark;


public class BankHttpRestApi
{
	private final BankJsonApi bankJsonApi;
	
	public BankHttpRestApi(BankJsonApi bankJsonApi)
	{
		this.bankJsonApi = bankJsonApi;
	}
	
	public void start()
	{
		Spark.ipAddress("0.0.0.0");
//		Spark.port(listenPort);
		
		configureRoutes();
		
		Spark.awaitInitialization();
	}
	
	private void configureRoutes()
	{
		// bank account stuff
		Spark.post("/bank/accounts", this::accountOpen);
		Spark.get("/bank/accounts", this::accountsList);
		Spark.get("/bank/accounts/:id", this::accountGetInfo);
		Spark.get("/bank/accounts/:id/balance", this::accountGetBalance);
		Spark.delete("/bank/accounts/:id", this::accountClose);
		
		Spark.post("/bank/transfers", this::transferExecute);
	}
	
	private String accountsList(Request request, Response response)
	{
		try
		{
			return bankJsonApi.accountsList();
		}
		catch (BankJsonApiInternalError ex)
		{
			Spark.halt(500);
			
			return null;
		}
	}
	
	private String accountOpen(Request request, Response response)
	{
		String accountDescription = request.body();
		
		try
		{
			return bankJsonApi.accountOpen(accountDescription);
		}
		catch (BankJsonApiInternalError ex)
		{
			Spark.halt(500);
			
			return null;
		}
	}
	
	private String accountGetInfo(Request request, Response response)
	{
		String id = request.params("id");
		
		return bankJsonApi.accountGetInfo(id);
	}
	
	private String accountGetBalance(Request request, Response response)
	{
		String id = request.params("id");
		
		return bankJsonApi.accountGetBalance(id);
	}
	
	private String accountClose(Request request, Response response)
	{
		String id = request.params("id");
		
		return bankJsonApi.accountClose(id);
	}
	
	private String transferExecute(Request request, Response response)
	{
		String transferDescription = request.body();
		
		return bankJsonApi.transferExecute(transferDescription);
	}
}
