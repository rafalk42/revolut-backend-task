package rafalk42.main;

import org.apache.commons.cli.*;
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
		CommandLine cmd = parseParameters(args);
		
		boolean functionalTest = cmd.hasOption("functionalTest");
		String addressValue = cmd.getOptionValue("address");
		String portValue = cmd.getOptionValue("port");
		
		if (functionalTest)
		{
			if (addressValue == null
				|| portValue == null)
			{
				System.out.println("Both address and port are required in functional test mode (use --help for more info).");
				System.exit(1);
			}
			
			int port = Integer.parseInt(portValue);
			
			startFunctionalTest(addressValue, port);
		}
		else
		{
			String listenAddress = "127.0.0.1";
			int listenPort = 4242;
			
			if (addressValue != null)
			{
				listenAddress = addressValue;
			}
			
			if (portValue != null)
			{
				listenPort = Integer.parseInt(portValue);
			}
			
			startServer(listenAddress, listenPort);
		}
	}
	
	/**
	 * Start the functional test - REST API client.
	 *
	 * @param address address of the HTTP REST API server to connect to
	 * @param port    port of the HTTP REST API server to connect to
	 */
	private static void startFunctionalTest(String address, int port)
	{
		final org.slf4j.Logger log = LoggerFactory.getLogger(Main.class);
		
		log.info(String.format("Starting functional test [Java:(%s %s) OS:(%s %s %s) encoding:%s]",
							   System.getProperty("java.vendor"), System.getProperty("java.version"), System.getProperty("os.arch"),
							   System.getProperty("os.name"), System.getProperty("os.version"), Charset.defaultCharset()));
		
		FunctionalTest test = new FunctionalTest(address, port);
		test.run();
	}
	
	/**
	 * Start the HTTP REST API server.
	 *
	 * @param listenAddress address on which to listen for incoming HTTP REST API requests
	 * @param listenPort    port on which to listen for incoming HTTP REST API requests
	 */
	private static void startServer(String listenAddress, int listenPort)
	{
		final org.slf4j.Logger log = LoggerFactory.getLogger(Main.class);
		
		log.info(String.format("Starting application [Java:(%s %s) OS:(%s %s %s) encoding:%s]",
							   System.getProperty("java.vendor"), System.getProperty("java.version"), System.getProperty("os.arch"),
							   System.getProperty("os.name"), System.getProperty("os.version"), Charset.defaultCharset()));
		
		AccountDao accountDao = new AccountDaoInMemory();
		Bank bank = new RockefellerBank(accountDao);
		BankJsonApi bankJsonApi = new BankJsonApi(bank);
		BankHttpRestApi bankHttpRestApi = new BankHttpRestApi(bankJsonApi);
		
		bankHttpRestApi.start(listenAddress, listenPort);
		
		log.info("Initialization complete");
	}
	
	/**
	 * Handle the setup of command line arguments parsing library and parsing itself. Returns the command line
	 * parser which allows one to retrieve parameters that were passed to the application.
	 * This method checks for invalid arguments and for use of help argument, in both cases prints usage info and
	 * terminates JVM with exit code 1.
	 *
	 * @param args raw command line arguments passed to the application
	 * @return command line parser
	 */
	private static CommandLine parseParameters(String[] args)
	{
		Options options = new Options();
		
		Option help = new Option("h", "help", false, "print usage");
		help.setRequired(false);
		options.addOption(help);
		
		Option functionalTest = new Option("f", "functionalTest", false, "start functional test");
		functionalTest.setRequired(false);
		options.addOption(functionalTest);
		
		Option address = new Option("a", "address", true, "address to listen on or connect to");
		address.setRequired(false);
		options.addOption(address);
		
		Option port = new Option("p", "port", true, "port to listen on or connect to");
		port.setRequired(false);
		options.addOption(port);
		
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		
		CommandLine cmd = null;
		try
		{
			cmd = parser.parse(options, args);
		}
		catch (ParseException e)
		{
			System.out.println(e.getMessage());
			formatter.printHelp("revolut-backend-task", options);
			
			System.exit(1);
		}
		
		if (cmd.hasOption("help"))
		{
			formatter.printHelp("revolut-backend-task", options);
			
			System.exit(1);
		}
		
		return cmd;
	}
	
	/**
	 * Set some configuration parameters for slf4j, so that it prints things we want, the way we want them!
	 */
	private static void configureSimpleLogger()
	{
//		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
		System.setProperty(SimpleLogger.SHOW_DATE_TIME_KEY, "true");
		System.setProperty(SimpleLogger.DATE_TIME_FORMAT_KEY, "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	}
}
