package rafalk42.main;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;


/**
 * A simple functional test using HTTP client to remotely (as in over the network, might be localhost)
 * execute commands on a bank REST API to verify that it works as designed.
 * The private classes below this one, which are DTOs for this client, are intentionally duplicated (the same stuff
 * exists in rafalk42.api.dto) so that this whole test is completely independent of the server implementation. In the
 * long run this is not a particularly great idea when you think about maintainability. Maybe the server side DTOs
 * should be reused here? Again, it's hard to say without any specific requirements, so let's just call this a
 * one way of doing this.
 */
public class FunctionalTest
{
	private final org.slf4j.Logger log = LoggerFactory.getLogger(FunctionalTest.class);
	private final String baseUrl;
	private final Gson gson;
	
	public FunctionalTest(String targetAddress, int targetPort)
	{
		baseUrl = String.format("http://%s:%d/bank/",
								targetAddress, targetPort);
		
		gson = new Gson();
	}
	
	/**
	 * Execute the test. All the info and errors will be logged with slf4j.
	 */
	public void run()
	{
		log.info(String.format("Base URL is %s",
							   baseUrl));
		
		try
		{
			String id1 = accountOpen("Blah blah one", BigDecimal.ZERO);
			String id2 = accountOpen("Blah blah two", BigDecimal.ZERO);
			
			List<AccountInfo> accountList = accountListAllInfo();
			log.info(accountList.toString());
			
			accountDeposit(id1, new DepositWithdrawDescription(BigDecimal.valueOf(1000), "No reason"));
			
			accountGetBalance(id1);
			
			transfer(new TransferDescription(id1, id2, BigDecimal.valueOf(1000)));
			
			accountGetBalance(id2);
			
			accountWithdraw(id2, new DepositWithdrawDescription(BigDecimal.valueOf(1000), "Still unknown"));
			
			accountList = accountListAllInfo();
			log.info(accountList.toString());
			
			accountClose(id1);
			accountClose(id2);
		}
		catch (UnirestException ex)
		{
			log.error("Error occurred", ex);
		}
	}
	
	private String accountOpen(String description, BigDecimal initialBalance)
			throws UnirestException
	{
		log.info(String.format("Open new account (description: \"%s\", initial balance: %s)",
							   description,
							   initialBalance.toPlainString()));
		
		AccountOpenDescription accountOpenDescription = new AccountOpenDescription(description,
																				   initialBalance);
		String accountDescriptionJson = gson.toJson(accountOpenDescription);
		HttpResponse<String> stringHttpResponse = Unirest.post(baseUrl + "accounts")
														 .body(accountDescriptionJson)
														 .asString();
		
		checkResponse(stringHttpResponse);
		
		String body = stringHttpResponse.getBody();
		AccountOpenResult accountOpenResult = gson.fromJson(body, AccountOpenResult.class);
		
		log.info(String.format("New account opened with ID \"%s\"",
							   accountOpenResult.getId()));
		
		return accountOpenResult.getId();
	}
	
	private List<AccountInfo> accountListAllInfo()
			throws UnirestException
	{
		log.info("Listing info of all accounts");
		
		HttpResponse<String> stringHttpResponse = Unirest.get(baseUrl + "accounts")
														 .asString();
		
		checkResponse(stringHttpResponse);
		
		String body = stringHttpResponse.getBody();
		List list = gson.fromJson(body, List.class);
		
		log.info(String.format("The list contains info on %d account(s)",
							   list.size()));
		
		return list;
	}
	
	private AccountInfo accountGetInfo(String accountId)
			throws UnirestException
	{
		log.info(String.format("Retrieving info on account ID \"%s\"",
							   accountId));
		
		HttpResponse<String> stringHttpResponse = Unirest.get(baseUrl + "accounts/{id}")
														 .routeParam("id", accountId)
														 .asString();
		
		checkResponse(stringHttpResponse);
		
		String body = stringHttpResponse.getBody();
		AccountInfo accountInfo = gson.fromJson(body, AccountInfo.class);
		
		log.info(String.format("Account ID \"%s\" has description \"%s\" and current balance of %s",
							   accountId,
							   accountInfo.getDescription(),
							   accountInfo.getBalance().toPlainString()));
		
		return accountInfo;
	}
	
	private BigDecimal accountGetBalance(String accountId)
			throws UnirestException
	{
		log.info(String.format("Retrieving balance of account ID \"%s\"",
							   accountId));
		
		HttpResponse<String> stringHttpResponse = Unirest.get(baseUrl + "accounts/{id}/balance")
														 .routeParam("id", accountId)
														 .asString();
		
		checkResponse(stringHttpResponse);
		
		String body = stringHttpResponse.getBody();
		BalanceInfo balanceInfo = gson.fromJson(body, BalanceInfo.class);
		
		
		log.info(String.format("Account ID \"%s\" has current balance of %s",
							   accountId,
							   balanceInfo.getBalance()));
		
		return balanceInfo.getBalance();
	}
	
	private DepositWithdrawResult accountDeposit(String accountId, DepositWithdrawDescription description)
			throws UnirestException
	{
		log.info(String.format("Depositing %s to account ID \"%s\" with title \"%s\"",
							   description.getAmount().toPlainString(),
							   accountId,
							   description.getTitle()));
		
		String requestBodyJson = gson.toJson(description);
		HttpResponse<String> stringHttpResponse = Unirest.post(baseUrl + "accounts/{id}/deposit")
														 .routeParam("id", accountId)
														 .body(requestBodyJson)
														 .asString();
		
		checkResponse(stringHttpResponse);
		
		String body = stringHttpResponse.getBody();
		DepositWithdrawResult result = gson.fromJson(body, DepositWithdrawResult.class);
		
		log.info(String.format("Result of depositing actual amount %s on account ID \"%s\" is %s",
							   result.getActualAmount(),
							   accountId,
							   result.getResult()));
		
		return result;
	}
	
	private DepositWithdrawResult accountWithdraw(String accountId, DepositWithdrawDescription description)
			throws UnirestException
	{
		log.info(String.format("Withdrawing %s from account ID \"%s\" with title \"%s\"",
							   description.getAmount().toPlainString(),
							   accountId,
							   description.getTitle()));
		
		String requestBodyJson = gson.toJson(description);
		HttpResponse<String> stringHttpResponse = Unirest.post(baseUrl + "accounts/{id}/withdraw")
														 .routeParam("id", accountId)
														 .body(requestBodyJson)
														 .asString();
		
		checkResponse(stringHttpResponse);
		
		String body = stringHttpResponse.getBody();
		DepositWithdrawResult result = gson.fromJson(body, DepositWithdrawResult.class);
		
		log.info(String.format("Result of withdrawing actual amount %s from account ID \"%s\" is %s",
							   result.getActualAmount(),
							   accountId,
							   result.getResult()));
		
		return result;
	}
	
	private void accountClose(String accountId)
			throws UnirestException
	{
		log.info(String.format("Closing account ID \"%s\"",
							   accountId));
		
		HttpResponse<String> stringHttpResponse = Unirest.delete(baseUrl + "accounts/{id}")
														 .routeParam("id", accountId)
														 .asString();
		
		checkResponse(stringHttpResponse);
		
		log.info(String.format("Account ID \"%s\" closed",
							   accountId));
	}
	
	private TransferResult transfer(TransferDescription description)
			throws UnirestException
	{
		log.info(String.format("Transferring %s from account ID \"%s\" to \"%s\"",
							   description.getAmount().toPlainString(),
							   description.getSourceAccountId(),
							   description.getDestinationAccountId()));
		
		String requestBodyJson = gson.toJson(description);
		HttpResponse<String> stringHttpResponse = Unirest.post(baseUrl + "transfers")
														 .body(requestBodyJson)
														 .asString();
		
		checkResponse(stringHttpResponse);
		
		String body = stringHttpResponse.getBody();
		TransferResult result = gson.fromJson(body, TransferResult.class);
		
		log.info(String.format("Result of transferring actual amount %s from account ID \"%s\" to \"%s\" is %s",
							   result.getActualAmount(),
							   description.getSourceAccountId(),
							   description.getDestinationAccountId(),
							   result.getResult()));
		
		return result;
	}
	
	private void checkResponse(HttpResponse response)
			throws UnirestException
	{
		if (response.getStatus() != 200)
		{
			throw new UnirestException(String.format("HTTP error status %d: %s",
													 response.getStatus(),
													 response.getStatusText()));
		}
	}
}


class AccountOpenDescription
{
	private final String description;
	private final BigDecimal initialBalance;
	
	AccountOpenDescription(String description, BigDecimal initialBalance)
	{
		this.description = description;
		this.initialBalance = initialBalance;
	}
}

class AccountOpenResult
{
	private final String id;
	
	private AccountOpenResult()
	{
		id = null;
	}
	
	public String getId()
	{
		return id;
	}
}

class AccountInfo
{
	private final String id;
	private final String description;
	private final BigDecimal balance;
	
	AccountInfo()
	{
		id = null;
		description = null;
		balance = null;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public BigDecimal getBalance()
	{
		return balance;
	}
	
	@Override
	public String toString()
	{
		return "AccountInfo{" +
			   "id='" + id + '\'' +
			   ", description='" + description + '\'' +
			   ", balance=" + balance +
			   '}';
	}
}

class BalanceInfo
{
	private final BigDecimal balance;
	
	BalanceInfo()
	{
		balance = null;
	}
	
	public BigDecimal getBalance()
	{
		return balance;
	}
}

class DepositWithdrawDescription
{
	private final BigDecimal amount;
	private final String title;
	
	DepositWithdrawDescription(BigDecimal amount, String title)
	{
		this.amount = amount;
		this.title = title;
	}
	
	public BigDecimal getAmount()
	{
		return amount;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	@Override
	public String toString()
	{
		return "DepositWithdrawDescription{" +
			   "amount=" + amount +
			   ", title='" + title + '\'' +
			   '}';
	}
}

class DepositWithdrawResult
{
	private final DepositWithdrawDescription request;
	private final BigDecimal actualAmount;
	private final String result;
	
	public DepositWithdrawResult()
	{
		request = null;
		actualAmount = null;
		result = null;
	}
	
	public DepositWithdrawDescription getRequest()
	{
		return request;
	}
	
	public BigDecimal getActualAmount()
	{
		return actualAmount;
	}
	
	public String getResult()
	{
		return result;
	}
	
	@Override
	public String toString()
	{
		return "DepositWithdrawResult{" +
			   "request=" + request +
			   ", actualAmount=" + actualAmount +
			   ", result='" + result + '\'' +
			   '}';
	}
}

class TransferDescription
{
	private final String sourceAccountId;
	private final String destinationAccountId;
	private final BigDecimal amount;
	
	TransferDescription(String sourceAccountId, String destinationAccountId, BigDecimal amount)
	{
		this.sourceAccountId = sourceAccountId;
		this.destinationAccountId = destinationAccountId;
		this.amount = amount;
	}
	
	public String getSourceAccountId()
	{
		return sourceAccountId;
	}
	
	public String getDestinationAccountId()
	{
		return destinationAccountId;
	}
	
	public BigDecimal getAmount()
	{
		return amount;
	}
}

class TransferResult
{
	private final TransferDescription request;
	private final BigDecimal actualAmount;
	private final String result;
	
	TransferResult()
	{
		request = null;
		actualAmount = null;
		result = null;
	}
	
	public TransferDescription getRequest()
	{
		return request;
	}
	
	public BigDecimal getActualAmount()
	{
		return actualAmount;
	}
	
	public String getResult()
	{
		return result;
	}
}