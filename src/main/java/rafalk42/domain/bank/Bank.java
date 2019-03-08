package rafalk42.domain.bank;

import java.util.Optional;


public interface Bank
{
	BankAccount accountCreate(BankAccountDescription accountDescription)
			throws BankInternalError;
	
	Optional<BankAccount> accountFindByStringId(String accountId)
			throws BankInternalError;
	
	Amount getBalance(BankAccount account)
			throws BankInternalError;
	
	TransferResult transfer(BankAccount sourceAccount, BankAccount destinationAccount, Amount amount)
			throws BankInternalError;
}
