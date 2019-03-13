package rafalk42.bank.rockefeller;

import rafalk42.bank.domain.*;
import rafalk42.dao.AccountDao;
import rafalk42.dao.AccountDaoInternalError;
import rafalk42.dao.AccountInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
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
	/**
	 *
	 */
	private final BigDecimal rulesMinimumTransferAmount = BigDecimal.valueOf(1, 2);
	
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
												  accountDescription.getInitialBalance());
			
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
	public Map<BankAccount, BankAccountInfo> accountsGetInfoAll()
			throws BankInternalError
	{
		try
		{
			transactionLock.lock();
			
			Set<AccountInfo> allAccounts = accountDao.findAll();
			
			Map<BankAccount, BankAccountInfo> result = new HashMap<>();
			allAccounts.forEach(item -> result.put(new RockefellerBankAccount(item.getId()),
												   new BankAccountInfo(item.getDescription(),
																	   item.getBalance())));
			
			return result;
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
	public BankAccountInfo accountGetInfo(BankAccount account)
			throws BankInternalError, BankAccountNotFound
	{
		verifyBankAccountImplementation(account);
		
		try
		{
			transactionLock.lock();
			
			String accountId = account.getId();
			if (!accountDao.doesItExist(accountId))
			{
				throw new BankAccountNotFound(accountId);
			}
			
			AccountInfo info = accountDao.getInfo(accountId);
			
			return new BankAccountInfo(info.getDescription(),
									   info.getBalance());
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
	public BigDecimal accountGetBalance(BankAccount account)
			throws BankInternalError, BankAccountNotFound
	{
		verifyBankAccountImplementation(account);
		
		try
		{
			transactionLock.lock();
			
			String accountId = account.getId();
			if (!accountDao.doesItExist(accountId))
			{
				throw new BankAccountNotFound(accountId);
			}
			
			return accountDao.getBalance(accountId);
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
	public OperationResult accountDeposit(BankAccount account, BigDecimal amount, String title)
			throws BankInternalError, BankAccountNotFound
	{
		verifyBankAccountImplementation(account);
		
		if (amount == null)
		{
			throw new IllegalArgumentException("Amount cannot be null");
		}
		
		try
		{
			String accountId = account.getId();
			
			transactionLock.lock();
			
			if (!accountDao.doesItExist(accountId))
			{
				throw new BankAccountNotFound(accountId);
			}
			
			return executeDeposit(accountId,
								  amount);
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
	public OperationResult accountWithdraw(BankAccount account, BigDecimal amount, String title)
			throws BankInternalError, BankAccountNotFound
	{
		verifyBankAccountImplementation(account);
		
		if (amount == null)
		{
			throw new IllegalArgumentException("Amount cannot be null");
		}
		
		try
		{
			String accountId = account.getId();
			
			transactionLock.lock();
			
			if (!accountDao.doesItExist(accountId))
			{
				throw new BankAccountNotFound(accountId);
			}
			
			return executeWithdraw(accountId,
								   amount);
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
	public OperationResult transferAmount(BankAccount sourceAccount, BankAccount destinationAccount, BigDecimal amount)
			throws BankInternalError, BankAccountNotFound
	{
		verifyBankAccountImplementation(sourceAccount);
		verifyBankAccountImplementation(destinationAccount);
		
		if (amount == null)
		{
			throw new IllegalArgumentException("Amount cannot be null");
		}
		
		try
		{
			String sourceAccountId = sourceAccount.getId();
			String destinationAccountId = destinationAccount.getId();
			
			transactionLock.lock();
			
			if (!accountDao.doesItExist(sourceAccountId))
			{
				throw new BankAccountNotFound(sourceAccountId);
			}
			
			if (!accountDao.doesItExist(destinationAccountId))
			{
				throw new BankAccountNotFound(destinationAccountId);
			}
			
			return executeTransfer(sourceAccountId,
								   destinationAccountId,
								   amount);
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
	 * For details see executeTransfer method.
	 *
	 * @param accountId account ID to which amount will be added
	 * @param amount    amount to accountDeposit
	 * @return result of this operation
	 * @throws AccountDaoInternalError thrown when underlying DAO failed due to unknown error
	 */
	private OperationResult executeDeposit(String accountId, BigDecimal amount)
			throws AccountDaoInternalError
	{
		// Verify assumption #2.
		if (amount.compareTo(rulesMinimumTransferAmount) < 0)
		{
			return OperationResult.getInvalidAmount(amount);
		}
		
		// Apply assumption #3.
		BigDecimal amountRounded = amount.setScale(2, RoundingMode.HALF_UP);
		
		BigDecimal balanceBefore = accountDao.getBalance(accountId);
		
		// Calculate new balance.
		BigDecimal balanceAfter = balanceBefore.add(amountRounded);
		
		// Update balance.
		accountDao.setBalance(accountId, balanceAfter);
		
		return OperationResult.getSuccessful(amountRounded);
	}
	
	/**
	 * For details see executeTransfer method.
	 *
	 * @param accountId account ID from which amount will be subtracted
	 * @param amount    amount to accountWithdraw
	 * @return result of this operation
	 * @throws AccountDaoInternalError thrown when underlying DAO failed due to unknown error
	 */
	private OperationResult executeWithdraw(String accountId, BigDecimal amount)
			throws AccountDaoInternalError
	{
		// Verify assumption #2.
		if (amount.compareTo(rulesMinimumTransferAmount) < 0)
		{
			return OperationResult.getInvalidAmount(amount);
		}
		
		// Apply assumption #3.
		BigDecimal amountRounded = amount.setScale(2, RoundingMode.HALF_UP);
		
		BigDecimal balanceBefore = accountDao.getBalance(accountId);
		
		// Calculate new balance.
		BigDecimal balanceAfter = balanceBefore.subtract(amountRounded);
		
		// Verify assumption #4.
		if (balanceAfter.compareTo(BigDecimal.ZERO) < 0)
		{
			return OperationResult.getNotEnoughFunds(amountRounded);
		}
		
		// Update balance.
		accountDao.setBalance(accountId, balanceAfter);
		
		return OperationResult.getSuccessful(amountRounded);
	}
	
	/**
	 * Actually execute the transfer. This method contains the business logic of a transfer.
	 * Assumptions:
	 * 1. source and destination account must be different,
	 * 2. amount transferred cannot be less than the rulesMinimumTransferAmount,
	 * 3. amount will be rounded half up (actually - half away from zero),
	 * 4. account cannot get into debt - that is the balance of source account cannot be negative after the transfer.
	 * <p>
	 * Of course a proper rule engine of some sort should be used for better maintainability, but this is explicit
	 * enough for the purpose of this implementation.
	 *
	 * @param sourceAccountId      account ID from which amount will be taken
	 * @param destinationAccountId account ID to which amount will be added
	 * @param amount               amount to transfer
	 * @return result of this operation
	 * @throws AccountDaoInternalError thrown when underlying DAO failed due to unknown error
	 */
	private OperationResult executeTransfer(String sourceAccountId, String destinationAccountId, BigDecimal amount)
			throws AccountDaoInternalError
	{
		// Verify assumption #1.
		if (sourceAccountId.equals(destinationAccountId))
		{
			return OperationResult.getNotAllowed(amount);
		}
		
		// Verify assumption #2.
		if (amount.compareTo(rulesMinimumTransferAmount) < 0)
		{
			return OperationResult.getInvalidAmount(amount);
		}
		
		// Apply assumption #3.
		BigDecimal amountRounded = amount.setScale(2, RoundingMode.HALF_UP);
		
		// Check current balances.
		BigDecimal balanceSourceBefore = accountDao.getBalance(sourceAccountId);
		BigDecimal balanceDestinationBefore = accountDao.getBalance(destinationAccountId);
		
		// Calculate new balances.
		BigDecimal balanceSourceAfter = balanceSourceBefore.subtract(amountRounded);
		BigDecimal balanceDestinationAfter = balanceDestinationBefore.add(amountRounded);
		
		// Verify assumption #4.
		if (balanceSourceAfter.compareTo(BigDecimal.ZERO) < 0)
		{
			return OperationResult.getNotEnoughFunds(amountRounded);
		}
		
		// Update balance on both accounts.
		accountDao.setBalance(sourceAccountId, balanceSourceAfter);
		accountDao.setBalance(destinationAccountId, balanceDestinationAfter);
		
		return OperationResult.getSuccessful(amountRounded);
	}
	
	@Override
	public void accountClose(BankAccount account)
			throws BankInternalError, BankAccountNotFound
	{
		verifyBankAccountImplementation(account);
		
		try
		{
			transactionLock.lock();
			
			String accountId = account.getId();
			
			if (!accountDao.doesItExist(accountId))
			{
				throw new BankAccountNotFound(accountId);
			}
			
			accountDao.close(accountId);
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
		if (bankAccount == null)
		{
			throw new IllegalArgumentException("Bank account cannot be null");
		}
		
		if (!(bankAccount instanceof RockefellerBankAccount))
		{
			throw new IllegalArgumentException("Unsupported implementation of BankAccount");
		}
	}
}
