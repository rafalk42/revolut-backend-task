package rafalk42.main;

import rafalk42.bank.domain.Bank;
import rafalk42.bank.rockefeller.RockefellerBank;
import rafalk42.dao.AccountDao;
import rafalk42.dao.AccountDaoInMemory;
import rafalk42.api.BankJsonApi;
import rafalk42.api.BankHttpRestApi;


public class Main
{
	public static void main(String[] args)
	{
		AccountDao accountDao = new AccountDaoInMemory();
		Bank bank = new RockefellerBank(accountDao);
		BankJsonApi bankJsonApi = new BankJsonApi(bank);
		BankHttpRestApi bankHttpRestApi = new BankHttpRestApi(bankJsonApi);
		
		bankHttpRestApi.start();
	}
}
