package rafalk42.dao;

import rafalk42.domain.dao.AccountDao;
import rafalk42.domain.dao.AccountDaoInternalError;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class AccountDaoInMemory
		implements AccountDao
{
	private final Map<String, AccountInMemory> accounts;
	private final AtomicInteger accountIdCounter;
	
	public AccountDaoInMemory()
	{
		accounts = new HashMap<>();
		accountIdCounter = new AtomicInteger(1);
	}
	
	@Override
	public String open(String description, BigDecimal initialBalance)
			throws AccountDaoInternalError
	{
		if (initialBalance == null)
		{
			throw new IllegalArgumentException("Initial balance cannot be null");
		}
		
		String newAccountId = getNextAccountId();
		AccountInMemory newAccount = new AccountInMemory(description, initialBalance);
		
		synchronized (accounts)
		{
			accounts.put(newAccountId, newAccount);
		}
		
		return newAccountId;
	}
	
	@Override
	public String getDescription(String accountId)
			throws AccountDaoInternalError
	{
		verifyAccountId(accountId);
		
		synchronized (accounts)
		{
			if (!accounts.containsKey(accountId))
			{
				throw new IllegalArgumentException("Account not found");
			}
			
			return accounts.get(accountId).getDescription();
		}
	}
	
	@Override
	public BigDecimal getBalance(String accountId)
			throws AccountDaoInternalError
	{
		verifyAccountId(accountId);
		
		synchronized (accounts)
		{
			if (!accounts.containsKey(accountId))
			{
				throw new IllegalArgumentException("Account not found");
			}
			
			return accounts.get(accountId).getBalance();
		}
	}
	
	@Override
	public void setBalance(String accountId, BigDecimal newBalance)
			throws AccountDaoInternalError
	{
		verifyAccountId(accountId);
		
		if (newBalance == null)
		{
			throw new IllegalArgumentException("New balance cannot be null");
		}
		
		synchronized (accounts)
		{
			if (!accounts.containsKey(accountId))
			{
				throw new IllegalArgumentException("Account not found");
			}
			accounts.get(accountId).setBalance(newBalance);
		}
	}
	
	@Override
	public void close(String accountId)
			throws AccountDaoInternalError
	{
		verifyAccountId(accountId);
		
		synchronized (accounts)
		{
			if (!accounts.containsKey(accountId))
			{
				throw new IllegalArgumentException("Account not found");
			}
			accounts.remove(accountId);
		}
	}
	
	@Override
	public boolean doesItExist(String accountId)
			throws AccountDaoInternalError
	{
		synchronized (accounts)
		{
			return accounts.containsKey(accountId);
		}
	}
	
	private String getNextAccountId()
	{
		return String.format("%010d", accountIdCounter.getAndIncrement());
	}
	
	private void verifyAccountId(String accountId)
	{
		if (accountId == null)
		{
			throw new IllegalArgumentException("Account ID cannot be null");
		}
	}
	
	private static class AccountInMemory
	{
		private final String description;
		private BigDecimal balance;
		
		AccountInMemory(String description, BigDecimal initialBalance)
		{
			this.description = description;
			balance = initialBalance;
		}
		
		public String getDescription()
		{
			return description;
		}
		
		BigDecimal getBalance()
		{
			return balance;
		}
		
		void setBalance(BigDecimal balance)
		{
			this.balance = balance;
		}
	}
}
