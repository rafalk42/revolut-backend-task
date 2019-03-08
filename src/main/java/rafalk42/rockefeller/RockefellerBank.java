package rafalk42.rockefeller;

import rafalk42.domain.bank.*;
import rafalk42.domain.dao.AccountDao;
import rafalk42.domain.dao.AccountDaoInternalError;

import java.math.BigDecimal;
import java.util.Optional;


public class RockefellerBank
		implements Bank
{
	private final AccountDao accountDao;
	
	public RockefellerBank(AccountDao accountDao)
	{
		this.accountDao = accountDao;
	}
	
	@Override
	public BankAccount accountCreate(BankAccountDescription accountDescription)
			throws BankInternalError
	{
		try
		{
			String newAccountId = accountDao.open(accountDescription.getDescription(),
												  accountDescription.getInitialBalance().getAsBigDecimal());
			
			return new RockefellerBankAccount(newAccountId);
		}
		catch (AccountDaoInternalError ex)
		{
			throw new BankInternalError(ex);
		}
	}
	
	@Override
	public Optional<BankAccount> accountFindByStringId(String accountId)
			throws BankInternalError
	{
		try
		{
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
	}
	
	@Override
	public Amount getBalance(BankAccount bankAccount)
			throws BankInternalError
	{
		verifyBankAccount(bankAccount);
		
		RockefellerBankAccount rockefellerBankAccount = (RockefellerBankAccount) bankAccount;
		String accountId = rockefellerBankAccount.getAccountId();
		try
		{
			accountDao.doesItExist(accountId);
			BigDecimal balance = accountDao.getBalance(accountId);
			
			return Amount.fromBigDecimal(balance);
		}
		catch (AccountDaoInternalError ex)
		{
			throw new BankInternalError(ex);
		}
	}
	
	@Override
	public TransferResult transfer(BankAccount sourceAccount, BankAccount destinationAccount, Amount amount)
			throws BankInternalError
	{
		return null;
	}
	
	private void verifyBankAccount(BankAccount bankAccount)
	{
		if (!(bankAccount instanceof RockefellerBankAccount))
		{
			throw new IllegalArgumentException("Unsupported implementation of BankAccount");
		}
	}
}
