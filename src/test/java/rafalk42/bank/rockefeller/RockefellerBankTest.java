package rafalk42.bank.rockefeller;

import org.junit.Before;
import org.junit.Test;
import rafalk42.bank.domain.BankAccount;
import rafalk42.bank.domain.BankAccountDescription;
import rafalk42.bank.domain.BankInternalError;
import rafalk42.dao.AccountDao;
import rafalk42.dao.AccountDaoInternalError;
import rafalk42.dao.AccountInfo;

import java.math.BigDecimal;
import java.util.Set;


public class RockefellerBankTest
{
	private RockefellerBank bank;
	
	@Before
	public void setUp()
			throws Exception
	{
		AccountDaoMock accountDaoMock = new AccountDaoMock();
		bank = new RockefellerBank(accountDaoMock);
	}
	
	@Test
	public void accountCreateTest()
			throws BankInternalError
	{
		BankAccountDescription accountDescription = new BankAccountDescription.Builder()
				.description("Paycheck")
				.initialBalance(75000)
				.build();
		
		BankAccount account = bank.accountCreate(accountDescription);
	}
}

class AccountDaoMock
		implements AccountDao
{
	
	@Override
	public String open(String description, BigDecimal initialBalance)
			throws AccountDaoInternalError
	{
		return null;
	}
	
	@Override
	public boolean doesItExist(String accountId)
			throws AccountDaoInternalError
	{
		return false;
	}
	
	@Override
	public Set<AccountInfo> findAll()
			throws AccountDaoInternalError
	{
		return null;
	}
	
	@Override
	public AccountInfo getInfo(String accountId)
			throws AccountDaoInternalError
	{
		return null;
	}
	
	@Override
	public BigDecimal getBalance(String accountId)
			throws AccountDaoInternalError
	{
		return null;
	}
	
	@Override
	public void setBalance(String accountId, BigDecimal newBalance)
			throws AccountDaoInternalError
	{
	
	}
	
	@Override
	public void close(String accountId)
			throws AccountDaoInternalError
	{
	
	}
}