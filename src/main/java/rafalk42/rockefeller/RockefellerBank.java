package rafalk42.rockefeller;

import rafalk42.bank.domain.*;
import rafalk42.dao.AccountDao;
import rafalk42.dao.AccountDaoInternalError;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class RockefellerBank
		implements Bank
{
	private final AccountDao accountDao;
	/**
	 * Let's simply use a per instance of this class lock, which is not great when it comes to performance
	 * (an operation will lock the whole bank, irregardless of the accounts involved),
	 * but it's very easy to follow and debug and good enough in this situation.
	 * Let's just say this is a rather sluggish bank.
	 * Added bonus is that we are not depending on the thread safety of the account DAO implementation.
	 */
	private final Lock transactionLock;
	
	public RockefellerBank(AccountDao accountDao)
	{
		this.accountDao = accountDao;
		
		transactionLock = new ReentrantLock();
	}
	
	@Override
	public BankAccount accountCreate(BankAccountDescription accountDescription)
			throws BankInternalError
	{
		try
		{
			transactionLock.lock();
			String newAccountId = accountDao.open(accountDescription.getDescription(),
												  accountDescription.getInitialBalance().getAsBigDecimal());
			
			return new RockefellerBankAccount(newAccountId);
		}
		catch (AccountDaoInternalError ex)
		{
			throw new BankInternalError(ex);
		}
		finally
		{
			transactionLock.unlock();
		}
	}
	
	@Override
	public Set<BankAccount> getAccountsAll()
			throws BankInternalError
	{
		return null;
	}
	
	@Override
	public Optional<BankAccount> accountFindById(String accountId)
			throws BankInternalError
	{
		try
		{
			transactionLock.lock();
			
			if (!accountDao.doesItExist(accountId))
			{
				return Optional.empty();
			}
			
			return Optional.of(new RockefellerBankAccount(accountId));
		}
		catch (AccountDaoInternalError ex)
		{
			throw new BankInternalError(ex);
		}
		finally
		{
			transactionLock.unlock();
		}
	}
	
	@Override
	public Amount getBalance(BankAccount account)
			throws BankInternalError
	{
		verifyBankAccountImplementation(account);
		
		try
		{
			transactionLock.lock();
			
			String accountId = account.getId();
			accountDao.doesItExist(accountId);
			BigDecimal balance = accountDao.getBalance(accountId);
			
			return Amount.fromBigDecimal(balance);
		}
		catch (AccountDaoInternalError ex)
		{
			throw new BankInternalError(ex);
		}
		finally
		{
			transactionLock.unlock();
		}
	}
	
	@Override
	public TransferResult transferAmount(BankAccount sourceAccount, BankAccount destinationAccount, Amount amount)
			throws BankInternalError
	{
		verifyBankAccountImplementation(sourceAccount);
		verifyBankAccountImplementation(destinationAccount);
		
		try
		{
			String sourceAccountId = sourceAccount.getId();
			String destinationAccountId = destinationAccount.getId();
			BigDecimal amountToTransfer = amount.getAsBigDecimal();
			
			transactionLock.lock();

			return doTransfer(sourceAccountId,
							  destinationAccountId,
							  amountToTransfer);
		}
		catch (AccountDaoInternalError ex)
		{
			throw new BankInternalError(ex);
		}
		finally
		{
			transactionLock.unlock();
		}
	}
	
	/**
	 * Actually execute the transfer. This method contains the business logic of a transfer.
	 * Assumptions:
	 * 1. amount transferred cannot be negative,
	 * 2. account cannot get into debt - that is the balance cannot be negative after the transfer.
	 * <p>
	 * Of course a proper rule engine of some sort should be used for better maintainability, but this is explicit
	 * enough for the purpose of this implementation.
	 *
	 * @param sourceAccountId      account ID from which amount will be taken
	 * @param destinationAccountId account ID to which amount will be added
	 * @param amount               amount to transfer
	 * @throws AccountDaoInternalError thrown when underlying DAO failed due to unknown error
	 */
	private TransferResult doTransfer(String sourceAccountId, String destinationAccountId, BigDecimal amount)
			throws AccountDaoInternalError
	{
		// Verify assumption #1.
		if (amount.compareTo(BigDecimal.ZERO) < 0)
		{
			return TransferResult.getInvalidAmount();
		}
		
		// check current balances
		BigDecimal beforeSourceBalance = accountDao.getBalance(sourceAccountId);
		BigDecimal beforeDestinationBalance = accountDao.getBalance(destinationAccountId);
		
		// calculate new balances
		BigDecimal afterSourceBalance = beforeSourceBalance.subtract(amount);
		BigDecimal afterDestinationBalance = beforeDestinationBalance.add(amount);
		
		// Verify assumption #2.
		if (afterSourceBalance.compareTo(BigDecimal.ZERO) < 0)
		{
			return TransferResult.getNotEnoughFunds();
		}
		
		accountDao.setBalance(sourceAccountId, afterSourceBalance);
		accountDao.setBalance(destinationAccountId, afterDestinationBalance);
		
		return TransferResult.getOk();
	}
	
	@Override
	public ClosureResult close(BankAccount account)
			throws BankInternalError
	{
		verifyBankAccountImplementation(account);
		
		try
		{
			transactionLock.lock();
			
			accountDao.close(account.getId());
			
			return new ClosureResult();
		}
		catch (AccountDaoInternalError ex)
		{
			throw new BankInternalError(ex);
		}
		finally
		{
			transactionLock.unlock();
		}
	}
	
	private void verifyBankAccountImplementation(BankAccount bankAccount)
	{
		if (!(bankAccount instanceof RockefellerBankAccount))
		{
			throw new IllegalArgumentException("Unsupported implementation of BankAccount");
		}
	}
}
