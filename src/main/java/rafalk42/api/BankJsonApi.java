package rafalk42.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import rafalk42.bank.domain.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


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
			
			BankAccount bankAccount = bank.accountCreate(bankAccountDescription);
			
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
			
			TransferResult transferResult = bank.transferAmount(sourceBankAccount.get(),
																destinationBankAccount.get(),
																amount);
			
			TransferResultDto result = new TransferResultDto(transferDescription,
															 transferResult.getActualAmount(),
															 transferResult.getStatusString());
			
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