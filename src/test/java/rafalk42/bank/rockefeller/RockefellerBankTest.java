package rafalk42.bank.rockefeller;

import org.junit.Before;
import org.junit.Test;
import rafalk42.bank.domain.*;
import rafalk42.dao.AccountDao;
import rafalk42.dao.AccountDaoInternalError;
import rafalk42.dao.AccountInfo;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * This is an incomplete suite of tests for the RockefellerBank implementation.
 * For brevity and due to time constrains, all error/failure scenarios have been omitted.
 * Also note that the method of testing used here - mock of the account DAO and deep introspection
 * of its use by the RockefellerBank - is tightly coupled with this particular Bank implementation.
 */
public class RockefellerBankTest
{
	private final String exampleAccountDescription = "Foo bar";
	private final BigDecimal exampleBalance = BigDecimal.valueOf(7500000, 2);
	private final BigDecimal exampleAmount = BigDecimal.valueOf(100000, 2);
	private final String exampleAccountId1 = "1234567890";
	private final String exampleAccountId2 = "0987654321";
	
	private RockefellerBank bank;
	private AccountDaoMock accountDaoMock;
	
	@Before
	public void setUp()
			throws Exception
	{
		accountDaoMock = new AccountDaoMock();
		bank = new RockefellerBank(accountDaoMock);
	}
	
	@Test
	public void accountOpenTest()
			throws BankInternalError
	{
		BankAccountDescription accountDescription = new BankAccountDescription.Builder()
				.description(exampleAccountDescription)
				.initialBalance(exampleBalance)
				.build();
		
		accountDaoMock.openResult = exampleAccountId1;
		BankAccount account = bank.accountCreate(accountDescription);
		
		assertEquals(1, accountDaoMock.openCallCounter); // open called once
		assertEquals(exampleAccountId1, account.getId()); // returned account ID matches
		assertEquals(exampleAccountDescription, accountDaoMock.openDescription); // account description matches
		assertEquals(exampleBalance, accountDaoMock.openInitialBalance); // account initial balance matches
	}
	
	@Test
	public void accountsGetInfoAllTest()
			throws BankInternalError
	{
		Set<AccountInfo> findAllResult = new HashSet<>();
		findAllResult.add(new AccountInfo(exampleAccountId1,
										  exampleAccountDescription,
										  exampleBalance));
		
		accountDaoMock.findAllResult = findAllResult;
		Map<BankAccount, BankAccountInfo> infoAll = bank.accountsGetInfoAll();
		
		
		assertEquals(1, accountDaoMock.findAllCallCounter); // called once
		assertEquals(1, infoAll.size()); // returns single item
		
		Map.Entry<BankAccount, BankAccountInfo> entry = infoAll.entrySet().iterator().next();
		assertEquals(exampleAccountId1, entry.getKey().getId()); // account ID matches
		assertEquals(exampleAccountDescription, entry.getValue().getDescription()); // account description matches
		assertEquals(exampleBalance, entry.getValue().getBalance()); // account balance matches
	}
	
	@Test
	public void accountFindByIdTest()
			throws BankInternalError
	{
		accountDaoMock.doestItExistResult = Arrays.asList(Boolean.TRUE);
		Optional<BankAccount> bankAccount = bank.accountFindById(exampleAccountId1);
		
		assertEquals(accountDaoMock.doestItExistResult.size(), accountDaoMock.doestItExistCallCounter); // called once
		assertTrue(bankAccount.isPresent()); // info returned
		assertEquals(exampleAccountId1, bankAccount.get().getId()); // account ID matches
	}
	
	@Test
	public void accountGetInfoTest()
			throws BankInternalError, BankAccountNotFound
	{
		accountDaoMock.doestItExistResult = Arrays.asList(Boolean.TRUE,
														  Boolean.TRUE);
		accountDaoMock.getInfoResult = new AccountInfo(exampleAccountId1,
													   exampleAccountDescription,
													   exampleBalance);
		
		Optional<BankAccount> bankAccount = bank.accountFindById(exampleAccountId1);
		BankAccountInfo bankAccountInfo = bank.accountGetInfo(bankAccount.get()); // no need to check ifPresent
		
		assertEquals(accountDaoMock.doestItExistResult.size(), accountDaoMock.doestItExistCallCounter); // called twice (in findById and in GetInfo)
		assertEquals(1, accountDaoMock.getInfoCallCounter); // called once
		assertEquals(exampleAccountId1, accountDaoMock.getInfoAccountId); // account ID matches
		assertEquals(exampleAccountDescription, bankAccountInfo.getDescription()); // account description matches
		assertEquals(exampleBalance, bankAccountInfo.getBalance()); // account balance matches
	}
	
	@Test
	public void accountGetBalanceTest()
			throws BankInternalError, BankAccountNotFound
	{
		accountDaoMock.doestItExistResult = Arrays.asList(Boolean.TRUE,
														  Boolean.TRUE);
		accountDaoMock.getBalanceResult = Arrays.asList(exampleBalance);
		
		Optional<BankAccount> bankAccount = bank.accountFindById(exampleAccountId1);
		BigDecimal balance = bank.accountGetBalance(bankAccount.get()); // no need to check ifPresent
		
		assertEquals(accountDaoMock.doestItExistResult.size(), accountDaoMock.doestItExistCallCounter); // called twice (in findById and in accountGetBalance)
		assertEquals(accountDaoMock.getBalanceResult.size(), accountDaoMock.getBalanceCallCounter); // called once
		assertEquals(exampleAccountId1, accountDaoMock.getBalanceAccountId.get(0)); // account ID matches
		assertEquals(exampleBalance, accountDaoMock.getBalanceResult.get(0)); // account balance matches
	}
	
	@Test
	public void transferAmountTest()
			throws BankInternalError, BankAccountNotFound
	{
		accountDaoMock.doestItExistResult = Arrays.asList(Boolean.TRUE,
														  Boolean.TRUE,
														  Boolean.TRUE,
														  Boolean.TRUE);
		accountDaoMock.getBalanceResult = Arrays.asList(exampleBalance,
														exampleBalance);
		
		Optional<BankAccount> sourceAccount = bank.accountFindById(exampleAccountId1);
		Optional<BankAccount> destinationAccount = bank.accountFindById(exampleAccountId2);
		TransferResult transferResult = bank.transferAmount(sourceAccount.get(), // no need to check ifPresent
															destinationAccount.get(), // no need to check ifPresent
															exampleAmount);
		
		assertEquals(accountDaoMock.doestItExistResult.size(), accountDaoMock.doestItExistCallCounter); // called four times (twice in findById and twice in transferAmount)
		assertEquals(accountDaoMock.getBalanceResult.size(), accountDaoMock.getBalanceCallCounter); // called twice, once for each account
		assertEquals(2, accountDaoMock.setBalanceCallCounter); // called twice, once for each account
		
		assertEquals(Arrays.asList(exampleAccountId1,
								   exampleAccountId2), accountDaoMock.setBalanceAccountId); // account IDs match
		
		BigDecimal expectedBalance1 = exampleBalance.subtract(exampleAmount);
		BigDecimal expectedBalance2 = exampleBalance.add(exampleAmount);
		assertEquals(Arrays.asList(expectedBalance1,
								   expectedBalance2), accountDaoMock.setBalanceNewBalance); // account balances match
		assertEquals(TransferResult.Status.SUCCESSFUL, transferResult.getStatus());
		assertEquals(exampleAmount, transferResult.getActualAmount());
	}
	
	@Test
	public void accountCloseTest()
			throws BankInternalError, BankAccountNotFound
	{
		accountDaoMock.doestItExistResult = Arrays.asList(Boolean.TRUE,
														  Boolean.TRUE);

		Optional<BankAccount> bankAccount = bank.accountFindById(exampleAccountId1);
		bank.accountClose(bankAccount.get()); // no need to check ifPresent
		
		assertEquals(accountDaoMock.doestItExistResult.size(), accountDaoMock.doestItExistCallCounter); // called twice (in findById and in accountClose)
		assertEquals(1, accountDaoMock.closeCallCounter); // called once
		assertEquals(exampleAccountId1, accountDaoMock.closeAccountId); // account ID matches
	}
}

/**
 * A simple mock in which you can set what value will be returned on method call
 * and check which methods were called with what parameters.
 * Fields are package-private to save on all those getters and setters, which in
 * this case would, in my opinion, not add anything beneficial.
 */
class AccountDaoMock
		implements AccountDao
{
	// open
	int openCallCounter = 0;
	String openDescription = null;
	BigDecimal openInitialBalance = null;
	String openResult = null;
	
	// findAll
	int findAllCallCounter = 0;
	Set<AccountInfo> findAllResult = null;
	
	// doesItExist
	int doestItExistCallCounter = 0;
	List<Boolean> doestItExistResult = new ArrayList<>();
	
	// getInfo
	int getInfoCallCounter = 0;
	String getInfoAccountId = null;
	AccountInfo getInfoResult = null;
	
	// getBalance
	int getBalanceCallCounter = 0;
	List<String> getBalanceAccountId = new ArrayList<>();
	List<BigDecimal> getBalanceResult = new ArrayList<>();
	
	// setBalance
	int setBalanceCallCounter = 0;
	List<String> setBalanceAccountId = new ArrayList<>();
	List<BigDecimal> setBalanceNewBalance = new ArrayList<>();
	
	// close
	int closeCallCounter = 0;
	String closeAccountId = null;
	
	@Override
	public String open(String description, BigDecimal initialBalance)
			throws AccountDaoInternalError
	{
		openCallCounter++;
		openDescription = description;
		openInitialBalance = initialBalance;
		
		return openResult;
	}
	
	@Override
	public boolean doesItExist(String accountId)
			throws AccountDaoInternalError
	{
		doestItExistCallCounter++;
		
		return doestItExistResult.get(doestItExistCallCounter - 1);
	}
	
	@Override
	public Set<AccountInfo> findAll()
			throws AccountDaoInternalError
	{
		findAllCallCounter++;
		
		return findAllResult;
	}
	
	@Override
	public AccountInfo getInfo(String accountId)
			throws AccountDaoInternalError
	{
		getInfoCallCounter++;
		getInfoAccountId = accountId;
		
		return getInfoResult;
	}
	
	@Override
	public BigDecimal getBalance(String accountId)
			throws AccountDaoInternalError
	{
		getBalanceCallCounter++;
		getBalanceAccountId.add(accountId);
		
		return getBalanceResult.get(getBalanceCallCounter - 1);
	}
	
	@Override
	public void setBalance(String accountId, BigDecimal newBalance)
			throws AccountDaoInternalError
	{
		setBalanceCallCounter++;
		setBalanceAccountId.add(accountId);
		setBalanceNewBalance.add(newBalance);
	}
	
	@Override
	public void close(String accountId)
			throws AccountDaoInternalError
	{
		closeCallCounter++;
		closeAccountId = accountId;
	}
}