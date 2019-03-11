package rafalk42.bank.domain;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


public interface Bank
{
	BankAccount accountCreate(BankAccountDescription accountDescription)
			throws BankInternalError;
	
	Set<BankAccount> accountsGetAll()
			throws BankInternalError;
	
	Map<BankAccount, BankAccountInfo> accountsGetInfoAll()
			throws BankInternalError;
	
	Optional<BankAccount> accountFindById(String accountId)
			throws BankInternalError;
	
	BankAccountInfo accountGetInfo(BankAccount account)
			throws BankInternalError, BankAccountNotFound;
	
	BigDecimal accountGetBalance(BankAccount account)
			throws BankInternalError, BankAccountNotFound;
	
	TransferResult transferAmount(BankAccount sourceAccount, BankAccount destinationAccount, BigDecimal amount)
			throws BankInternalError, BankAccountNotFound;
	
	void accountClose(BankAccount account)
			throws BankInternalError, BankAccountNotFound;
}
