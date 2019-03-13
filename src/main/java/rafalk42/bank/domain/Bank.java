package rafalk42.bank.domain;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;


/**
 * A model of a simple bank, without any authentication, authorization or any concept of a user actually.
 * There's also no support for things like inter-bank transfers.
 * Entities:
 * - BankAccount: representing a single, valid account. A handler, if you prefer.
 * Couple things could be designed a bit better, but without any particular requirements I say this is good enough.
 * This interface is probably much more complicated than necessary for the purpose of this task, but I wanted something
 * sensible and semi-realistic.
 */
public interface Bank
{
	/**
	 * Open new account based on the information from account description.
	 *
	 * @param accountDescription a set of information required for setting up the new account
	 * @return the handler for newly opened account
	 * @throws BankInternalError thrown when unknown error occurred
	 */
	BankAccount accountOpen(BankAccountDescription accountDescription)
			throws BankInternalError;
	
	/**
	 * Retrieve detailed information about all accounts.
	 *
	 * @return a map of handlers and detailed information about each account
	 * @throws BankInternalError thrown when unknown error occurred
	 */
	Map<BankAccount, BankAccountInfo> accountsGetInfoAll()
			throws BankInternalError;
	
	/**
	 * Retrieve a handler for an account with given ID.
	 *
	 * @param accountId ID of desired account
	 * @return the handler for account, if found
	 * @throws BankInternalError thrown when unknown error occurred
	 */
	Optional<BankAccount> accountFindById(String accountId)
			throws BankInternalError;
	
	/**
	 * Retrieve detailed information about a single account.
	 *
	 * @param account a handler for account
	 * @return detailed information about account
	 * @throws BankInternalError   thrown when unknown error occurred
	 * @throws BankAccountNotFound thrown when account pointed by the handler doesn't exist
	 */
	BankAccountInfo accountGetInfo(BankAccount account)
			throws BankInternalError, BankAccountNotFound;
	
	/**
	 * Retrieve current balance of an account.
	 *
	 * @param account a handler for account
	 * @return current balance
	 * @throws BankInternalError   thrown when unknown error occurred
	 * @throws BankAccountNotFound thrown when account pointed by the handler doesn't exist
	 */
	BigDecimal accountGetBalance(BankAccount account)
			throws BankInternalError, BankAccountNotFound;
	
	/**
	 * Deposit given amount to an account, additionally associating a title (description) with this transaction.
	 *
	 * @param account a handler for account
	 * @param amount  amount to deposit
	 * @param title   a description of this transaction
	 * @return detailed information about the result of this transaction
	 * @throws BankInternalError   thrown when unknown error occurred
	 * @throws BankAccountNotFound thrown when account pointed by the handler doesn't exist
	 */
	OperationResult accountDeposit(BankAccount account, BigDecimal amount, String title)
			throws BankInternalError, BankAccountNotFound;
	
	/**
	 * Withdraw given amount from an account, additionally associating a title (description) with this transaction.
	 *
	 * @param account a handler for account
	 * @param amount  amount to withdraw
	 * @param title   a description of this transaction
	 * @return detailed information about the result of this transaction
	 * @throws BankInternalError   thrown when unknown error occurred
	 * @throws BankAccountNotFound thrown when account pointed by the handler doesn't exist
	 */
	OperationResult accountWithdraw(BankAccount account, BigDecimal amount, String title)
			throws BankInternalError, BankAccountNotFound;
	
	/**
	 * Transfer given amount from source to destination account.
	 *
	 * @param sourceAccount      account from which given amount will be taken
	 * @param destinationAccount account to which given amount will be added
	 * @param amount             amount ot transfer
	 * @return detailed information about the result of this transaction
	 * @throws BankInternalError   thrown when unknown error occurred
	 * @throws BankAccountNotFound thrown when account pointed by the handler doesn't exist
	 */
	OperationResult transferAmount(BankAccount sourceAccount, BankAccount destinationAccount, BigDecimal amount)
			throws BankInternalError, BankAccountNotFound;
	
	/**
	 * Close given account, completely discarding any funds that might be left on it.
	 *
	 * @param account a handler for account
	 * @throws BankInternalError   thrown when unknown error occurred
	 * @throws BankAccountNotFound thrown when account pointed by the handler doesn't exist
	 */
	void accountClose(BankAccount account)
			throws BankInternalError, BankAccountNotFound;
}
