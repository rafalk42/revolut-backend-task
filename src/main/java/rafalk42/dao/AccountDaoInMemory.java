package rafalk42.dao;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * A simple in-memory data store, an implementation of account DAO.
 * Important:
 * 1. it does not support any kind of persistence,
 * 2. it is NOT thread-safe by any means,
 * 3. it does NOT impose any business rules, just some null checks.
 */
public class AccountDaoInMemory
		implements AccountDao
{
	private final Map<String, AccountInMemory> accounts;
	private int accountIdCounter;
	
	public AccountDaoInMemory()
	{
		accounts = new HashMap<>();
		accountIdCounter = 1;
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
		
		accounts.put(newAccountId, newAccount);
		
		return newAccountId;
	}
	
	@Override
	public boolean doesItExist(String accountId)
			throws AccountDaoInternalError
	{
		if (accountId == null)
		{
			throw new IllegalArgumentException("Account ID cannot be null");
		}
		
		return accounts.containsKey(accountId);
	}
	
	@Override
	public Set<AccountInfo> findAll()
			throws AccountDaoInternalError
	{
		return accounts.entrySet()
				.stream()
				.map(entry -> new AccountInfo(entry.getKey(),
											  entry.getValue().description,
											  entry.getValue().balance))
				.collect(Collectors.toSet());
	}
	
	@Override
	public AccountInfo getInfo(String accountId)
			throws AccountDaoInternalError
	{
		verifyAccountId(accountId);
		
		if (!accounts.containsKey(accountId))
		{
			throw new IllegalArgumentException("Account not found");
		}
		
		AccountInMemory account = accounts.get(accountId);
		return new AccountInfo(accountId,
							   account.getDescription(),
							   account.getBalance());
	}
	
	@Override
	public BigDecimal getBalance(String accountId)
			throws AccountDaoInternalError
	{
		verifyAccountId(accountId);
		
		if (!accounts.containsKey(accountId))
		{
			throw new IllegalArgumentException("Account not found");
		}
		
		return accounts.get(accountId)
				.getBalance();
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
		
		if (!accounts.containsKey(accountId))
		{
			throw new IllegalArgumentException("Account not found");
		}
		accounts.get(accountId).setBalance(newBalance);
	}
	
	@Override
	public void close(String accountId)
			throws AccountDaoInternalError
	{
		verifyAccountId(accountId);
		
		if (!accounts.containsKey(accountId))
		{
			throw new IllegalArgumentException("Account not found");
		}
		accounts.remove(accountId);
	}
	
	private String getNextAccountId()
	{
		return String.format("%010d", accountIdCounter++);
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
		
		String getDescription()
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
