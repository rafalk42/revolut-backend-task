package rafalk42.bank.domain;

import java.util.Optional;
import java.util.Set;


public interface Bank
{
	BankAccount accountCreate(BankAccountDescription accountDescription)
			throws BankInternalError;
	
	Set<BankAccount> getAccountsAll()
			throws BankInternalError;
	
	Optional<BankAccount> accountFindById(String accountId)
			throws BankInternalError;
	
	Amount getBalance(BankAccount account)
			throws BankInternalError;
	
	TransferResult transferAmount(BankAccount sourceAccount, BankAccount destinationAccount, Amount amount)
			throws BankInternalError;
	
	ClosureResult close(BankAccount account)
			throws BankInternalError;
}
