package com.david.bank.transaction;

import com.david.bank.BankAccount;
import com.david.bank.BankAccountService;
import com.david.bank.Money;
import com.david.bank.TimeService;
import com.david.bank.currency.CurrencyCode;
import com.david.bank.currency.CurrencyService;
import com.david.bank.dao.BankAccountDao;
import com.david.bank.dao.Dao;
import com.david.bank.dao.PersistedTransactionDao;
import com.david.bank.exception.BankAccountNotExistsException;
import com.david.bank.exception.CurrencyNotSupportException;
import com.david.bank.exception.InvalidMoneyAmountException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ManagedBean;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Business service for money transfer transaction
 */
@ManagedBean
@Singleton
public class TransactionService {
	private final static Logger logger = LogManager.getLogger(TransactionService.class);

	//public members is not a good practice for java
	//but I think it's ok for service injection
	public TimeService timeService;

	public BankAccountService bankAccountService;

	public CurrencyService currencyService;

	public BankAccountDao bankAccountDao;

	public PersistedTransactionDao persistedTransactionDao;

	private PendingTransactionQueue pendingTransactionQueue = PendingTransactionQueue.getInstance();

	private long transactionTimeoutMS = 5000;

	//--------------------------------------------------
	//business logic methods for transactions

	/**
	 * transfer money between two bank accounts,
	 * on the agreed exchange rate(by sender) for a particular currency
	 *
	 * this should be a transactional operation between two accounts
	 *
	 * @param transaction @see com.david.bank.transaction.TransferTransaction for details
	 * @return a processed transaction with a unique transaction number
	 */
	public PersistedTransaction processTransfer(PersistedTransaction transaction) {
		assert(null != transaction);

		if (logger.isDebugEnabled()) {
			logger.debug(
					"process money transfer from " +
							transaction.getSenderAccountNumber() + " to " +
							transaction.getReceiverAccountNumber() + " for " +
							transaction.getMoney().getAmount() + " " +
							transaction.getMoney().getCurrency()
			);
		}

		//transaction timeout if requestedTime is too old
		Date requestedTime = transaction.getRequestedTime();
		Date processedTime = timeService.now();

		transaction.setProcessedTime(processedTime);

		long pendingTime = processedTime.getTime() - requestedTime.getTime();

		if (pendingTime > transactionTimeoutMS) {
			logger.warn("transaction " + transaction + " is pending too long:" + pendingTime + "ms > "+transactionTimeoutMS+"ms");
			transaction.setStatus(TransactionStatus.TIMEOUT);
			transaction.setFailedReason("timeout: pending too long");
			persistedTransactionDao.save(transaction);
			return transaction;
		}

		//check if the currency is supported
		final Money money = transaction.getMoney();
		checkCurrency(money);

		BankAccount senderAccount = getBankAccount(transaction.getSenderAccountNumber());
		BankAccount receiverAccount = getBankAccount(transaction.getReceiverAccountNumber());

		//transactional start
		bankAccountDao.lock(senderAccount);
		bankAccountDao.lock(receiverAccount);

		try {
			//these lines of code simulate transactional since there is no real transactional
			//for the in-memony datastore
			Money senderBalanceOriginal = senderAccount.getBalance();
			Money receiverBalanceOriginal = receiverAccount.getBalance();

			try {
				bankAccountService.withdrawal(senderAccount, money);
				bankAccountService.deposit(receiverAccount, money);
				transaction.setStatus(TransactionStatus.SUCCESS);
			} catch (Exception e) {
				senderAccount.setBalance(senderBalanceOriginal);
				receiverAccount.setBalance(receiverBalanceOriginal);

				transaction.setStatus(TransactionStatus.FAILED);
				transaction.setFailedReason(e.getMessage());
			}

			persistedTransactionDao.save(transaction);
		} finally {
			bankAccountDao.unlock(receiverAccount);
			bankAccountDao.unlock(senderAccount);
		}

		//transactional end

		return transaction;
	}

	/**
	 * set the request time of a newly money transfer transaction
	 * and append it to the consumer queue
	 *
	 * @param transaction a transaction request
	 * @return a pending transaction object
	 */
	public TransferTransaction requestTransfer(TransferTransaction transaction) {
		if (logger.isDebugEnabled()) {
			logger.debug(
					"request money transfer from " +
							transaction.getSenderAccountNumber() + " to " +
							transaction.getReceiverAccountNumber() + " for " +
							transaction.getMoney().getAmount() + " " +
							transaction.getMoney().getCurrency()
			);
		}

		String transactionNumber = persistedTransactionDao.generateTransactionNumber();
		Date requestedTime = timeService.now();

		PersistedTransaction persistedTransaction = new PersistedTransaction(transaction, transactionNumber, requestedTime);
		persistedTransaction = persistedTransactionDao.save(persistedTransaction);
		pendingTransactionQueue.add(persistedTransaction);

		logger.debug("queue size = " + pendingTransactionQueue.size());

		return persistedTransaction;
	}


	/**
	 * validate if all fields in the transaction is correct base on business logic
	 * @param transaction the target transaction to be validated
	 * @return true if there is no errors
	 * @throws BankAccountNotExistsException when bank account for sender or receiver not exists
	 * @throws CurrencyNotSupportException if currency to transfer is not supported
	 * @throws InvalidMoneyAmountException if the money amount is invalid eg. sender has no sufficient fund
	 */
	public boolean validateTransaction(TransferTransaction transaction) throws
		BankAccountNotExistsException,
		CurrencyNotSupportException,
		InvalidMoneyAmountException
	{
		//check if bank accounts exists
		BankAccount senderAccount = getBankAccount(transaction.getSenderAccountNumber());
		BankAccount receiverAccount = getBankAccount(transaction.getReceiverAccountNumber());

		//check if the currency is currently support
		final Money money = transaction.getMoney();
		checkCurrency(money);

		//check if the transfer amount is valid
		currencyService.subtract(senderAccount.getBalance(), transaction.getMoney());

		return true;
	}


	//--------------------------------------------------
	//methods to get/list transaction history
	//TODO: pagination, filter(by date, status, receiver, sender, etc), sorting

	/**
	 * @param transactionNumber unique identifier of the transaction
	 * @return the processed transaction quarried by the transactionNumber
	 */
	public PersistedTransaction getTransaction(String transactionNumber) {
		return persistedTransactionDao.get(transactionNumber);
	}


	/**
	 *
	 * @return all the transaction history in the system for the account
	 */
	public List<PersistedTransaction> getAllTransactionHistory() {
		return persistedTransactionDao.getAll();
	}

	/**
	 * @param accountNumber unique identifier of the account
	 * @return all transaction history of the particular account
	 */
	public List<PersistedTransaction> getAllTransactionHistory(String accountNumber) {

		List allReceivedHistory = getAllReceivedTransactionHistory(accountNumber);
		List allSentHistory = getAllSentTransactionHistory(accountNumber);

		List allHistory = new ArrayList<PersistedTransaction>();
		allHistory.addAll(allReceivedHistory);
		allHistory.addAll(allSentHistory);

		return allHistory;
	}

	/**
	 * @param accountNumber unique identifier of the account
	 * @return all transaction history for the money sent by the particular account
	 */
	public List<PersistedTransaction> getAllSentTransactionHistory(String accountNumber) {
		//check the existence of the account
		getBankAccount(accountNumber);
		return persistedTransactionDao.getAllBySender(accountNumber);
	}

	/**
	 * @param accountNumber
	 * @return all transaction history for the money received by the particular account
	 */
	public List<PersistedTransaction> getAllReceivedTransactionHistory(String accountNumber) {
		//check the existence of the account
		getBankAccount(accountNumber);
		return persistedTransactionDao.getAllByReceiver(accountNumber);
	}


	//--------------------------------------------------
	//private helpers

	private BankAccount getBankAccount(String accountNumber) {
		try {
			return bankAccountDao.get(accountNumber);
		} catch(Dao.EntityNotFoundException e) {
			throw new BankAccountNotExistsException(accountNumber);
		}
	}

	private void checkCurrency(Money money) {
		final CurrencyCode currency = money.getCurrency();
		boolean isCurrencySupport = currencyService.isCurrencySupport(currency);
		if (!isCurrencySupport) {
			throw new CurrencyNotSupportException(currency.name());
		}
	}



	//--------------------------------------------------
	//setters

	public void setTransactionTimeoutSeconds(long transactionTimeoutSeconds) {
		this.transactionTimeoutMS = 1000 * transactionTimeoutSeconds;
	}
}
