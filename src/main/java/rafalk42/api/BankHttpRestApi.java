package rafalk42.api;

import com.google.gson.Gson;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;


public class BankHttpRestApi
{
	final org.slf4j.Logger log = LoggerFactory.getLogger(BankHttpRestApi.class);
	private final BankJsonApi bankJsonApi;
	private final Gson gson;
	
	public BankHttpRestApi(BankJsonApi bankJsonApi)
	{
		this.bankJsonApi = bankJsonApi;
		
		gson = new Gson();
	}
	
	public void start()
	{
		log.info("Starting HTTP server");
		
		Spark.ipAddress("0.0.0.0");
//		Spark.port(listenPort);
		
		Spark.before((request, response) -> response.type("application/json"));
		
		configureRoutes();
		
		Spark.awaitInitialization();
		
		log.info("HTTP server ready");
	}
	
	private void configureRoutes()
	{
		Route halterMethodNotAllowed = (request, response) -> Spark.halt(HttpStatus.CLIENT_ERROR_METHOD_NOT_ALLOWED.get(),
																		 getErrorJson("Method not allowed"));
		Route halterUrlNotFound = (request, response) ->
		{
			response.status(HttpStatus.CLIENT_ERROR_NOT_FOUND.get());
			return getErrorJson("Not found");
		};
		
		String defaultContentType = "application/json";
		
		// Set default returned content type to JSON.
		Spark.before((request, response) -> response.type(defaultContentType));
		
		Spark.before(this::logRequest);
		Spark.path("/bank", () ->
		{
			Spark.path("/accounts", () ->
			{
				Spark.get("", defaultContentType, this::accountsList);
				Spark.get("/:id", defaultContentType, this::accountGetInfo);
				Spark.get("/:id/balance", defaultContentType, this::accountGetBalance);
				Spark.post("", defaultContentType, this::accountOpen);
				Spark.delete("/:id", defaultContentType, this::accountClose);
				
				Spark.put("", defaultContentType, halterMethodNotAllowed);
				Spark.patch("", defaultContentType, halterMethodNotAllowed);
				Spark.delete("", defaultContentType, halterMethodNotAllowed);
				
				Spark.post("/:id", defaultContentType, halterMethodNotAllowed);
				Spark.put("/:id", defaultContentType, halterMethodNotAllowed);
				Spark.patch("/:id", defaultContentType, halterMethodNotAllowed);
			});
			Spark.path("/transfers", () ->
			{
				Spark.post("", defaultContentType, this::transferExecute);
				
				Spark.put("", defaultContentType, halterMethodNotAllowed);
				Spark.patch("", defaultContentType, halterMethodNotAllowed);
				Spark.delete("", defaultContentType, halterMethodNotAllowed);
			});
		});
		
		Spark.notFound(halterUrlNotFound);
		
		Spark.exception(BankJsonApiInternalError.class, this::handleInternalError);
		Spark.exception(BankJsonApiEntityNotFound.class, this::handleEntityNotFound);
		Spark.exception(BankJsonApiInvalidParameter.class, this::handleInvalidParameter);
	}
	
	private void logRequest(Request request, Response response)
	{
		log.info(String.format("HTTP request from %s: %s %s",
							   request.ip(),
							   request.requestMethod(),
							   request.pathInfo()));
	}
	
	private void handleInternalError(BankJsonApiInternalError ex, Request request, Response response)
	{
		response.status(HttpStatus.SERVER_ERROR_INTERNAL.get());
		response.body(getErrorJson(ex.getMessage()));
	}
	
	private void handleEntityNotFound(BankJsonApiEntityNotFound ex, Request request, Response response)
	{
		response.status(HttpStatus.CLIENT_ERROR_NOT_FOUND.get());
		response.body(getErrorJson(ex.getMessage()));
	}
	
	private void handleInvalidParameter(BankJsonApiInvalidParameter ex, Request request, Response response)
	{
		response.status(HttpStatus.CLIENT_ERROR_BAD_REQUEST.get());
		response.body(getErrorJson(ex.getMessage()));
	}
	
	private String accountsList(Request request, Response response)
			throws BankJsonApiInternalError
	{
		return bankJsonApi.accountsList();
	}
	
	private String accountOpen(Request request, Response response)
			throws BankJsonApiInvalidParameter, BankJsonApiInternalError
	{
		String accountDescription = request.body();
		
		return bankJsonApi.accountOpen(accountDescription);
	}
	
	private String accountGetInfo(Request request, Response response)
			throws BankJsonApiInternalError, BankJsonApiEntityNotFound
	{
		String id = request.params("id");
		
		return bankJsonApi.accountGetInfo(id);
	}
	
	private String accountGetBalance(Request request, Response response)
			throws BankJsonApiInternalError, BankJsonApiEntityNotFound
	{
		String id = request.params("id");
		
		return bankJsonApi.accountGetBalance(id);
	}
	
	private String accountClose(Request request, Response response)
			throws BankJsonApiInternalError, BankJsonApiEntityNotFound
	{
		String id = request.params("id");
		
		return bankJsonApi.accountClose(id);
	}
	
	private String transferExecute(Request request, Response response)
			throws BankJsonApiInternalError, BankJsonApiInvalidParameter, BankJsonApiEntityNotFound
	{
		String transferDescription = request.body();
		
		return bankJsonApi.transferExecute(transferDescription);
	}
	
	private String getErrorJson(String message)
	{
		return gson.toJson(new ApiErrorDto(message));
	}
}
