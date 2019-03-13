package rafalk42.dao;

import java.math.BigDecimal;
import java.util.Set;


/**
 * A simple DAO layer interface that keeps track of accounts and their balance.
 */
public interface AccountDao
{
	/**
	 * Open new account with given description and initial balance, returning ID of newly created account.
	 *
	 * @param description    a text describing account
	 * @param initialBalance initial balance that the account will have after creation
	 * @return ID of newly created account
	 * @throws AccountDaoInternalError thrown when unknown error occurred
	 */
	String open(String description, BigDecimal initialBalance)
			throws AccountDaoInternalError;
	
	/**
	 * Check if an account with given ID exists, returning true if id does, false otherwise.
	 *
	 * @param accountId ID of an account to check
	 * @return true if the account with given ID exists, false otherwise
	 * @throws AccountDaoInternalError thrown when unknown error occurred
	 */
	boolean doesItExist(String accountId)
			throws AccountDaoInternalError;
	
	/**
	 * Retrieve detailed information about all available accounts.
	 *
	 * @return a set information about all available accounts
	 * @throws AccountDaoInternalError thrown when unknown error occurred
	 */
	Set<AccountInfo> findAll()
			throws AccountDaoInternalError;
	
	/**
	 * Retrieve detailed information about a single account with given ID.
	 *
	 * @param accountId ID of an account for which to retrieve information
	 * @return detailed information about an account with given ID
	 * @throws AccountDaoInternalError thrown when unknown error occurred
	 */
	AccountInfo getInfo(String accountId)
			throws AccountDaoInternalError;
	
	/**
	 * Retrieve balance of an account with given ID.
	 *
	 * @param accountId ID of an account for which to retrieve balance
	 * @return balance of an account with given ID
	 * @throws AccountDaoInternalError thrown when unknown error occurred
	 */
	BigDecimal getBalance(String accountId)
			throws AccountDaoInternalError;
	
	/**
	 * Set balance of an account with given ID.
	 *
	 * @param accountId  ID of an account which balance will be set
	 * @param newBalance new balance to set on the account
	 * @throws AccountDaoInternalError thrown when unknown error occurred
	 */
	void setBalance(String accountId, BigDecimal newBalance)
			throws AccountDaoInternalError;
	
	/**
	 * Close an account with giben ID.
	 *
	 * @param accountId ID of an account which will be close
	 * @throws AccountDaoInternalError thrown when unknown error occurred
	 */
	void close(String accountId)
			throws AccountDaoInternalError;
}
