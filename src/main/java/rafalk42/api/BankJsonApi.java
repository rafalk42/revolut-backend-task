package rafalk42.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import rafalk42.bank.domain.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
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
			throws BankJsonApiInternalError
	{
		// TODO: add input data validation
		AccountDescriptionDto accountDescription = gson.fromJson(accountDescriptionJson, AccountDescriptionDto.class);
		
		BankAccountDescription bankAccountDescription = new BankAccountDescription.Builder()
				.description(accountDescription.getDescription())
				.initialBalance(Amount.fromBigDecimal(accountDescription.getInitialBalance()))
				.build();
		
		try
		{
			BankAccount bankAccount = bank.accountCreate(bankAccountDescription);
			
			AccountOpenResultDto result = new AccountOpenResultDto(bankAccount.getId());
			
			return gson.toJson(result);
		}
		catch (BankInternalError ex)
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
																							.getBalance()
																							.getAsBigDecimal()))
													  .collect(Collectors.toList());
			
			return gson.toJson(result);
		}
		catch (BankInternalError ex)
		{
			throw new BankJsonApiInternalError(ex);
		}
	}
	
	String accountGetInfo(String id)
	{
		return "{\"id\":\"12345524332\",\"description\":\"Foo bar\"}";
	}
	
	String accountGetBalance(String id)
	{
		return "{\"balance\":1234.34}";
	}
	
	String accountClose(String id)
	{
		return "{}";
	}
	
	String transferExecute(String transferDescription)
	{
		// TODO: add input data validation

		return "{\"result\":\"SUCCESS\"}";
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