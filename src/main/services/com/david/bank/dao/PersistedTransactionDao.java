package com.david.bank.dao;

import com.david.bank.transaction.PersistedTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Singleton
public class PersistedTransactionDao implements Dao<PersistedTransaction, String> {
	private final static Logger logger = LogManager.getLogger(PersistedTransactionDao.class);

	private final static int TRANSACTION_NUMBER_LENGH = 18;

	private static Map<String, PersistedTransaction> transactions = new ConcurrentHashMap<>();
	private Random rand = new Random();

	public PersistedTransaction get(String transactionNumber) throws EntityNotFoundException {
		PersistedTransaction transaction = transactions.get(transactionNumber);
		if (null == transaction) {
			throw new EntityNotFoundException(getClass(), transactionNumber);
		}

		return transaction;
	}

	public List<PersistedTransaction> getAll() {
		return new ArrayList<>(transactions.values());
	}

	public List<PersistedTransaction> getAllBySender(String accountNumber) {
		return getAll().stream()
				.filter(t->t.getSenderAccountNumber().equals(accountNumber))
				.collect(Collectors.toList());
	}

	public List<PersistedTransaction> getAllByReceiver(String accountNumber) {
		return getAll().stream()
				.filter(t->t.getReceiverAccountNumber().equals(accountNumber))
				.collect(Collectors.toList());
	}

	public PersistedTransaction save(PersistedTransaction persistedTransaction) {
		assert (persistedTransaction != null);

		final String transactionNumber = persistedTransaction.getTransactionNumber();
		logger.debug("save persistedTransaction " +  transactionNumber);
		transactions.put(transactionNumber, persistedTransaction);
		return persistedTransaction;
	}

	public void delete(PersistedTransaction persistedTransaction) throws EntityNotFoundException {
		final String transactionNumber = persistedTransaction.getTransactionNumber();

		logger.debug("delete persistedTransaction " +  transactionNumber);

		PersistedTransaction removed = transactions.remove(transactionNumber);
		if (null == removed) {
			throw new EntityNotFoundException(getClass(), transactionNumber);
		}
	}

	/**
	 * @return an unique transaction Number
	 */
	public String generateTransactionNumber() {
		//FIXME: In practice we may need the help from the database to generate it or
		// using some sophisticated hash function on random numbers
		for(int i=0; i<1000; ++i) {
			Long number = rand.nextLong();
			if (number < 0) number = -number;

			String numberStr = String.valueOf(number);
			while (numberStr.length() <= TRANSACTION_NUMBER_LENGH) {
				numberStr = "0" + numberStr;
			}
			String transactionNumber = numberStr.substring(0, TRANSACTION_NUMBER_LENGH);

			try {
				get(transactionNumber);
			} catch (EntityNotFoundException e) {
				return transactionNumber;
			}
		}

		throw new RuntimeException("tried too many times for generating unique transaction number");
	}
}
