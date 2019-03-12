package rafalk42.main;

import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLogger;
import rafalk42.api.BankHttpRestApi;
import rafalk42.api.BankJsonApi;
import rafalk42.bank.domain.Bank;
import rafalk42.bank.rockefeller.RockefellerBank;
import rafalk42.dao.AccountDao;
import rafalk42.dao.AccountDaoInMemory;

import java.nio.charset.Charset;


public class Main
{
	public static void main(String[] args)
	{
		configureSimpleLogger();
		
		final org.slf4j.Logger log = LoggerFactory.getLogger(Main.class);
		
		log.info(String.format("Starting application [Java:(%s %s) OS:(%s %s %s) encoding:%s]",
							   System.getProperty("java.vendor"), System.getProperty("java.version"), System.getProperty("os.arch"),
							   System.getProperty("os.name"), System.getProperty("os.version"), Charset.defaultCharset()));
		
		AccountDao accountDao = new AccountDaoInMemory();
		Bank bank = new RockefellerBank(accountDao);
		BankJsonApi bankJsonApi = new BankJsonApi(bank);
		BankHttpRestApi bankHttpRestApi = new BankHttpRestApi(bankJsonApi);
		
		bankHttpRestApi.start();
		
		log.info("Initialization complete");
	}
	
	private static void configureSimpleLogger()
	{
//		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
		System.setProperty(SimpleLogger.SHOW_DATE_TIME_KEY, "true");
		System.setProperty(SimpleLogger.DATE_TIME_FORMAT_KEY, "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	}
}
