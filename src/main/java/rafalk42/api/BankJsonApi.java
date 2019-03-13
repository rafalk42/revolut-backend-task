package rafalk42.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import rafalk42.api.dto.*;
import rafalk42.bank.domain.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * This is an adapter for the Bank interface that wraps inputs and outputs as JSON strings, so that
 * all the serialization and deserialization happens in here. I'm pretty sure that there's a much
 * better way of doing this, with less code duplication and better maintainability, but let's just
 * roll with this. She'll be right.
 *
 * It should be pretty easy and beneficial to create comprehensive unit tests for this layer, which
 * would be a great way to test the HTTP REST API without actually using HTTP server. From here up
 * to the REST API we are only adding the HTTP layer, not touching the JSON strings at all.
 *
 * Tests were not created due to a lack of time.
 */
public class BankJsonApi
{
	private final Bank bank;
	private final Gson gson;
	
	public BankJsonApi(Bank bank)
	{
		this.bank = bank;
		
		gson = new GsonBuilder().registerTypeAdapter(BigDecimal.class,
													 new BigDecimalTypeAdapter())
								.create();
	}
	
	String accountOpen(String accountDescriptionJson)
			throws BankJsonApiInternalError, BankJsonApiInvalidParameter
	{
		try
		{
			AccountDescriptionDto accountDescription = gson.fromJson(accountDescriptionJson, AccountDescriptionDto.class);
			
			if (accountDescription.getInitialBalance() == null)
			{
				throw new BankJsonApiInvalidParameter("Missing parameter: initialBalance");
			}
			
			BankAccountDescription bankAccountDescription = new BankAccountDescription.Builder()
					.description(accountDescription.getDescription())
					.initialBalance(accountDescription.getInitialBalance())
					.build();
			
			BankAccount bankAccount = bank.accountOpen(bankAccountDescription);
			
			AccountOpenResultDto result = new AccountOpenResultDto(bankAccount.getId());
			
			return gson.toJson(result);
		}
		catch (JsonSyntaxException ex)
		{
			throw new BankJsonApiInvalidParameter("Request body is not a valid JSON format");
		}
		catch (BankJsonApiInvalidParameter ex)
		{
			throw ex;
		}
		catch (Throwable ex)
		{
			throw new BankJsonApiInternalError(ex);
		}
	}
	
	String accountsList()
			throws BankJsonApiInternalError
	{
		try
		{
			Map<BankAccount, BankAccountInfo> bankAccounts = bank.accountsGetInfoAll();
			
			List<AccountInfoDto> result = bankAccounts.entrySet()
													  .stream()
													  .map(entry -> new AccountInfoDto(entry.getKey().getId(),
																					   entry.getValue()
																							.getDescription(),
																					   entry.getValue()
																							.getBalance()))
													  .collect(Collectors.toList());
			
			return gson.toJson(result);
		}
		catch (Throwable ex)
		{
			throw new BankJsonApiInternalError(ex);
		}
	}
	
	String accountGetInfo(String id)
			throws BankJsonApiInternalError, BankJsonApiEntityNotFound
	{
		try
		{
			Optional<BankAccount> bankAccount = bank.accountFindById(id);
			
			if (!bankAccount.isPresent())
			{
				throw new BankJsonApiEntityNotFound(String.format("Account ID %s not found",
																  id));
			}
			
			BankAccountInfo bankAccountInfo = bank.accountGetInfo(bankAccount.get());
			AccountInfoDto accountInfo = new AccountInfoDto(id,
															bankAccountInfo.getDescription(),
															bankAccountInfo.getBalance());
			
			return gson.toJson(accountInfo);
		}
		catch (BankAccountNotFound ex)
		{
			throw new BankJsonApiEntityNotFound(String.format("Account ID %s not found",
															  ex.getAccountId()));
		}
		catch (BankJsonApiEntityNotFound ex)
		{
			throw ex;
		}
		catch (Throwable ex)
		{
			throw new BankJsonApiInternalError(ex);
		}
	}
	
	String accountGetBalance(String id)
			throws BankJsonApiInternalError, BankJsonApiEntityNotFound
	{
		try
		{
			Optional<BankAccount> bankAccount = bank.accountFindById(id);
			
			if (!bankAccount.isPresent())
			{
				throw new BankJsonApiEntityNotFound(String.format("Account ID %s not found",
																  id));
			}
			
			BankAccountInfo bankAccountInfo = bank.accountGetInfo(bankAccount.get());
			AccountBalanceDto accountBalance = new AccountBalanceDto(bankAccountInfo.getBalance());
			
			return gson.toJson(accountBalance);
		}
		catch (BankAccountNotFound ex)
		{
			throw new BankJsonApiEntityNotFound(String.format("Account ID %s not found",
															  ex.getAccountId()));
		}
		catch (BankJsonApiEntityNotFound ex)
		{
			throw ex;
		}
		catch (Throwable ex)
		{
			throw new BankJsonApiInternalError(ex);
		}
	}
	
	String accountClose(String id)
			throws BankJsonApiInternalError, BankJsonApiEntityNotFound
	{
		try
		{
			Optional<BankAccount> bankAccount = bank.accountFindById(id);
			
			if (!bankAccount.isPresent())
			{
				throw new BankJsonApiEntityNotFound(String.format("Account ID %s not found",
																  id));
			}
			
			bank.accountClose(bankAccount.get());
			
			return gson.toJson(new Object());
		}
		catch (BankAccountNotFound ex)
		{
			throw new BankJsonApiEntityNotFound(String.format("Account ID %s not found",
															  ex.getAccountId()));
		}
		catch (BankJsonApiEntityNotFound ex)
		{
			throw ex;
		}
		catch (Throwable ex)
		{
			throw new BankJsonApiInternalError(ex);
		}
	}
	
	String accountDeposit(String id, String depositDescriptionJson)
			throws BankJsonApiInternalError, BankJsonApiEntityNotFound, BankJsonApiInvalidParameter
	{
		return executeDepositOrWithdraw(id, depositDescriptionJson, bank::accountDeposit);
	}
	
	String accountWithdraw(String id, String withdrawDescriptionJson)
			throws BankJsonApiInternalError, BankJsonApiEntityNotFound, BankJsonApiInvalidParameter
	{
		return executeDepositOrWithdraw(id, withdrawDescriptionJson, bank::accountWithdraw);
	}
	
	@FunctionalInterface
	private interface DepositOrWithdrawOperation
	{
		OperationResult execute(BankAccount account, BigDecimal amount, String title)
				throws BankInternalError, BankAccountNotFound;
	}
	
	private String executeDepositOrWithdraw(String accountId, String depositWithdrawDescriptionJson, DepositOrWithdrawOperation operation)
			throws BankJsonApiInternalError, BankJsonApiEntityNotFound, BankJsonApiInvalidParameter
	{
		try
		{
			DepositWithdrawDescriptionDto withdrawDescription = gson.fromJson(depositWithdrawDescriptionJson, DepositWithdrawDescriptionDto.class);
			
			if (accountId == null)
			{
				throw new BankJsonApiInvalidParameter("Missing parameter: accountId");
			}
			
			if (withdrawDescription.getAmount() == null)
			{
				throw new BankJsonApiInvalidParameter("Missing parameter: amount");
			}
			
			Optional<BankAccount> bankAccount = bank.accountFindById(accountId);
			
			if (!bankAccount.isPresent())
			{
				throw new BankJsonApiInvalidParameter("Account not found");
			}
			
			OperationResult operationResult = operation.execute(bankAccount.get(),
																withdrawDescription.getAmount(),
																withdrawDescription.getTitle());
			
			DepositWithdrawResultDto result = new DepositWithdrawResultDto(withdrawDescription,
																		   operationResult.getActualAmount(),
																		   operationResult.getStatus().name());
			
			return gson.toJson(result);
			
		}
		catch (JsonSyntaxException ex)
		{
			throw new BankJsonApiInvalidParameter("Request body is not a valid JSON format");
		}
		catch (BankAccountNotFound ex)
		{
			throw new BankJsonApiEntityNotFound(String.format("Account ID %s not found",
															  ex.getAccountId()));
		}
		catch (BankJsonApiInvalidParameter ex)
		{
			throw ex;
		}
		catch (Throwable ex)
		{
			throw new BankJsonApiInternalError(ex);
		}
	}
	
	String transferExecute(String transferDescriptionJson)
			throws BankJsonApiInternalError, BankJsonApiEntityNotFound, BankJsonApiInvalidParameter
	{
		try
		{
			TransferDescriptionDto transferDescription = gson.fromJson(transferDescriptionJson, TransferDescriptionDto.class);
			
			if (transferDescription.getSourceAccountId() == null)
			{
				throw new BankJsonApiInvalidParameter("Missing parameter: sourceAccountId");
			}
			
			if (transferDescription.getDestinationAccountId() == null)
			{
				throw new BankJsonApiInvalidParameter("Missing parameter: destinationAccountId");
			}
			
			if (transferDescription.getAmount() == null)
			{
				throw new BankJsonApiInvalidParameter("Missing parameter: amount");
			}
			
			Optional<BankAccount> sourceBankAccount = bank.accountFindById(transferDescription.getSourceAccountId());
			Optional<BankAccount> destinationBankAccount = bank.accountFindById(transferDescription.getDestinationAccountId());
			BigDecimal amount = transferDescription.getAmount();
			
			if (!sourceBankAccount.isPresent())
			{
				throw new BankJsonApiInvalidParameter("Source account not found");
			}
			
			if (!destinationBankAccount.isPresent())
			{
				throw new BankJsonApiInvalidParameter("Destination account not found");
			}
			
			OperationResult operationResult = bank.transferAmount(sourceBankAccount.get(),
																  destinationBankAccount.get(),
																  amount);
			
			TransferResultDto result = new TransferResultDto(transferDescription,
															 operationResult.getActualAmount(),
															 operationResult.getStatus().name());
			
			return gson.toJson(result);
		}
		catch (JsonSyntaxException ex)
		{
			throw new BankJsonApiInvalidParameter("Request body is not a valid JSON format");
		}
		catch (BankAccountNotFound ex)
		{
			throw new BankJsonApiEntityNotFound(String.format("Account ID %s not found",
															  ex.getAccountId()));
		}
		catch (BankJsonApiInvalidParameter ex)
		{
			throw ex;
		}
		catch (Throwable ex)
		{
			throw new BankJsonApiInternalError(ex);
		}
	}
}

/**
 * Gson serialization and deserialization adapter for BigDecimal type. Uses half up rounding
 * and 2 decimal places of precision.
 */
class BigDecimalTypeAdapter
		extends TypeAdapter<BigDecimal>
{
	@Override
	public void write(JsonWriter jsonWriter, BigDecimal value)
			throws IOException
	{
		jsonWriter.value(value.setScale(2,
										RoundingMode.HALF_UP).toPlainString());
	}
	
	@Override
	public BigDecimal read(JsonReader jsonReader)
			throws IOException
	{
		String value = jsonReader.nextString();
		
		return new BigDecimal(value);
	}
}