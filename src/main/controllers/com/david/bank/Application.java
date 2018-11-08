package com.david.bank;

import com.david.bank.currency.CurrencyService;
import com.david.bank.dao.BankAccountDao;
import com.david.bank.dao.PersistedTransactionDao;
import com.david.bank.transaction.TransactionService;
import com.david.bank.transaction.TransferTransactionJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.validation.ValidationFeature;

import java.io.IOException;
import java.net.URI;

/**
 * Main class. ie. the start of the web application
 * Start a web server providing RESTFul API on money transfer between bank accounts
 */
public class Application {
	private final static Logger logger = LogManager.getLogger(Application.class);

	//depended data access objects
	private BankAccountDao bankAccountDao = new BankAccountDao();
	private PersistedTransactionDao persistedTransactionDao = new PersistedTransactionDao();

	//depended services
	private TimeService timeService = new TimeService();
	private BankAccountService bankAccountService = new BankAccountService();
	private CurrencyService currencyService = new CurrencyService();
	private TransactionService transactionService = new TransactionService();

	//in practice, there should be multiple consumers consuming the transactions base on
	//their account numbers(sender/receiver).
	//here for demo purpose only simulate a single consumer in the same application
	private TransferTransactionJob consumer = new TransferTransactionJob();

	public Application() {
		injectDependency();
	}

	private void injectDependency() {
		bankAccountService.bankAccountDao = bankAccountDao;
		bankAccountService.currencyService = currencyService;

		transactionService.timeService = timeService;
		transactionService.currencyService = currencyService;
		transactionService.persistedTransactionDao = persistedTransactionDao;
		transactionService.bankAccountDao = bankAccountDao;
		transactionService.bankAccountService = bankAccountService;

		consumer.transactionService = transactionService;
	}

	// Base URI the Grizzly HTTP server will listen on
	private String getBaseUri() {
		final String URI = "http://localhost";

		//default port is 3000
		String portStr = System.getProperty("port", "3000");

		//exception would be thrown if the input port is not integer
		int port = Integer.valueOf(portStr);

		return URI + ":" + port + "/";

	}

	/**
	 * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
	 * @return Grizzly HTTP server.
	 */
	public HttpServer startServer() {
		// create a resource config that scans for JAX-RS resources and providers
		final ResourceConfig rc = new ResourceConfig().packages("com.david.bank");

		//to support bean(request json body) validation
		rc.register(ValidationFeature.class);

		// Now you can expect validation errors to be sent to the client.
		rc.property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);

		// @ValidateOnExecution annotations on subclasses won't cause errors.
		rc.property(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK, true);


		//to support json
		rc.register(JacksonFeature.class);

		//dependencies injection to Jersey Resources
		rc.register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(bankAccountDao).to(BankAccountDao.class);
				bind(persistedTransactionDao).to(PersistedTransactionDao.class);

				bind(timeService).to(TimeService.class);
				bind(bankAccountService).to(BankAccountService.class);
				bind(currencyService).to(CurrencyService.class);
				bind(transactionService).to(TransactionService.class);
			}
		});

		//start the transaction job for consuming pending transactions in queue
		consumer.scheduleAtFixedRate(500);

		// create and start a new instance of grizzly http server
		// exposing the Jersey application at BASE_URI
		return GrizzlyHttpServerFactory.createHttpServer(URI.create(getBaseUri()), rc);
	}


	public void stopServer() {
		consumer.stop();
	}

	/**
	 * Main method.
	 * @param args parameters from command line console
	 * @throws IOException when stdin got interrupted
	 */
	public static void main(String[] args) throws IOException {
		Application app = new Application();
		HttpServer server = app.startServer();

		logger.info("Jersey app started at " + app.getBaseUri());

		System.out.println("Hit enter to stop it...");
		System.in.read();

		server.shutdownNow();
		app.stopServer();
	}

}