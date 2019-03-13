package rafalk42.bank.domain;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;


/**
 *
 */
public interface Bank
{
	BankAccount accountCreate(BankAccountDescription accountDescription)
			throws BankInternalError;
	
	Map<BankAccount, BankAccountInfo> accountsGetInfoAll()
			throws BankInternalError;
	
	Optional<BankAccount> accountFindById(String accountId)
			throws BankInternalError;
	
	BankAccountInfo accountGetInfo(BankAccount account)
			throws BankInternalError, BankAccountNotFound;
	
	BigDecimal accountGetBalance(BankAccount account)
			throws BankInternalError, BankAccountNotFound;
	
	OperationResult accountDeposit(BankAccount account, BigDecimal amount, String title)
			throws BankInternalError, BankAccountNotFound;
	
	OperationResult accountWithdraw(BankAccount account, BigDecimal amount, String title)
			throws BankInternalError, BankAccountNotFound;
	
	OperationResult transferAmount(BankAccount sourceAccount, BankAccount destinationAccount, BigDecimal amount)
			throws BankInternalError, BankAccountNotFound;
	
	void accountClose(BankAccount account)
			throws BankInternalError, BankAccountNotFound;
}
