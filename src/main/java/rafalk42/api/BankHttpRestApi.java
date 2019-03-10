package rafalk42.api;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Spark;


public class BankHttpRestApi
{
	private final BankJsonApi bankJsonApi;
	private final Gson gson;
	
	public BankHttpRestApi(BankJsonApi bankJsonApi)
	{
		this.bankJsonApi = bankJsonApi;
		
		gson = new Gson();
	}
	
	public void start()
	{
		Spark.ipAddress("0.0.0.0");
//		Spark.port(listenPort);
		
		Spark.before((request, response) -> response.type("application/json"));
		
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
			halt(500, ex.getMessage());
			
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
		catch (BankJsonApiInvalidParameter ex)
		{
			halt(400, ex.getMessage());
			
			return null;
		}
		catch (BankJsonApiInternalError ex)
		{
			halt(500, ex.getMessage());
			
			return null;
		}
	}
	
	private String accountGetInfo(Request request, Response response)
	{
		String id = request.params("id");
		
		try
		{
			return bankJsonApi.accountGetInfo(id);
		}
		catch (BankJsonApiEntityNotFound ex)
		{
			halt(404, ex.getMessage());
			
			return null;
		}
		catch (BankJsonApiInternalError ex)
		{
			halt(500, ex.getMessage());
			
			return null;
		}
	}
	
	private String accountGetBalance(Request request, Response response)
	{
		String id = request.params("id");
		
		try
		{
			return bankJsonApi.accountGetBalance(id);
		}
		catch (BankJsonApiEntityNotFound ex)
		{
			halt(404, ex.getMessage());
			
			return null;
		}
		catch (BankJsonApiInternalError ex)
		{
			halt(500, ex.getMessage());
			
			return null;
		}
	}
	
	private String accountClose(Request request, Response response)
	{
		String id = request.params("id");
		
		try
		{
			return bankJsonApi.accountClose(id);
		}
		catch (BankJsonApiEntityNotFound ex)
		{
			halt(404, ex.getMessage());
			
			return null;
		}
		catch (BankJsonApiInternalError ex)
		{
			halt(500, ex.getMessage());
			
			return null;
		}
	}
	
	private String transferExecute(Request request, Response response)
	{
		String transferDescription = request.body();
		
		try
		{
			return bankJsonApi.transferExecute(transferDescription);
		}
		catch (BankJsonApiInvalidParameter ex)
		{
			halt(400, ex.getMessage());
			
			return null;
		}
		catch (BankJsonApiEntityNotFound ex)
		{
			halt(404, ex.getMessage());
			
			return null;
		}
		catch (BankJsonApiInternalError ex)
		{
			halt(500, ex.getMessage());
			
			return null;
		}
	}
	
	private void halt(int status, String message)
	{
		String errorJson = gson.toJson(new ApiErrorDto(message));
		Spark.halt(status, errorJson);
	}
}
