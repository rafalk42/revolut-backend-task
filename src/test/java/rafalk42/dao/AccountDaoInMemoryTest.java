package rafalk42.dao;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;


public class AccountDaoInMemoryTest
{
	private final String exampleAccountDescription1 = "Foo bar";
	private final String exampleAccountDescription2 = "Fizz buzz";
	private final String exampleAccountDescription3 = "5318008";
	private final BigDecimal exampleInitialBalance1 = BigDecimal.valueOf(100.00);
	private final BigDecimal exampleInitialBalance2 = BigDecimal.valueOf(110.00);
	private final BigDecimal exampleInitialBalance3 = BigDecimal.valueOf(120.00);
	private final String nonexistentAccountId = "This ID has 99.9999% chance of not existing";
	
	private AccountDaoInMemory dao;
	private String accountId1;
	private String accountId2;
	private String accountId3;
	
	@Before
	public void setUp()
			throws Exception
	{
		// Create an instance and open 3 example accounts.
		dao = new AccountDaoInMemory();
		accountId1 = dao.open(exampleAccountDescription1, exampleInitialBalance1);
		accountId2 = dao.open(exampleAccountDescription2, exampleInitialBalance2);
		accountId3 = dao.open(exampleAccountDescription3, exampleInitialBalance3);
	}
	
	@Test
	public void openAndDoesExistTest()
			throws AccountDaoInternalError
	{
		
		assertTrue(dao.doesItExist(accountId1));
	}
	
	@Test
	public void closeAndDoesExistTest()
			throws AccountDaoInternalError
	{
		dao.close(accountId1);
		
		assertFalse(dao.doesItExist(accountId1));
	}
	
	@Test
	public void getInfoTest()
			throws AccountDaoInternalError
	{
		AccountInfo info = dao.getInfo(accountId1);
		
		// Check if all the info that we provided when creating account is correctly kept.
		assertEquals(info.getId(), accountId1);
		assertEquals(info.getDescription(), exampleAccountDescription1);
		assertEquals(info.getBalance(), exampleInitialBalance1);
	}
	
	@Test
	public void getBalanceTest()
			throws AccountDaoInternalError
	{
		BigDecimal balance = dao.getBalance(accountId1);
		
		// This partially overlaps with the previous test, but checks different function.
		assertEquals(balance, exampleInitialBalance1);
	}
	
	@Test
	public void setBalanceTest()
			throws AccountDaoInternalError
	{
		BigDecimal newBalance = exampleInitialBalance1.add(exampleInitialBalance1);
		dao.setBalance(accountId1, newBalance);
		BigDecimal balanceAfter = dao.getBalance(accountId1);
		
		assertEquals(balanceAfter, newBalance);
	}
	
	@Test
	public void findAllTest()
			throws AccountDaoInternalError
	{
		Set<AccountInfo> allAccounts = dao.findAll();
		
		Set<String> allAccountsIds = new HashSet<>(Arrays.asList(accountId1,
																 accountId2,
																 accountId3));
		Set<String> foundAccountIds = allAccounts.stream()
												 .map(AccountInfo::getId)
												 .collect(Collectors.toSet());
		
		assertEquals(foundAccountIds.size(), 3);
		assertEquals(foundAccountIds, allAccountsIds);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void openWithNullInitialBalanceTest()
			throws AccountDaoInternalError
	{
		dao.open(exampleAccountDescription1, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void getInfoWithNonexistentAccountId()
			throws AccountDaoInternalError
	{
		dao.getInfo(nonexistentAccountId);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void getBalanceWithNonexistentAccountId()
			throws AccountDaoInternalError
	{
		dao.getBalance(nonexistentAccountId);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setBalanceWithNonexistentAccountId()
			throws AccountDaoInternalError
	{
		dao.setBalance(nonexistentAccountId, BigDecimal.ZERO);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void closeWithNonexistentAccountId()
			throws AccountDaoInternalError
	{
		dao.close(nonexistentAccountId);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void doesItExistWithNullAccountId()
			throws AccountDaoInternalError
	{
		dao.doesItExist(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void getInfoWithNullAccountId()
			throws AccountDaoInternalError
	{
		dao.getInfo(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void getBalanceWithNullAccountId()
			throws AccountDaoInternalError
	{
		dao.getBalance(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setBalanceWithNullAccountId()
			throws AccountDaoInternalError
	{
		dao.setBalance(null, BigDecimal.ZERO);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setBalanceWithNullAmount()
			throws AccountDaoInternalError
	{
		dao.setBalance(accountId1, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void closeWithNullAccountId()
			throws AccountDaoInternalError
	{
		dao.close(null);
	}
}