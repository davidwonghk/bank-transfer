package com.david.bank.transaction;

import com.david.bank.TimerJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Consumer to consume pending transaction peroidically
 */
public class TransferTransactionJob extends TimerJob {

	private final static Logger logger = LogManager.getLogger(TransferTransactionJob.class);

	private PendingTransactionQueue pendingTransactionQueue = PendingTransactionQueue.getInstance();

	public TransactionService transactionService;


	@Override
	public void run() {
		PersistedTransaction transaction = null;
		while ( (transaction = pendingTransactionQueue.poll()) != null ) {
			logger.debug("consuming transaction " + transaction);
			try {
				transactionService.processTransfer(transaction);
			} catch (Exception e) {
				logger.error("error in consumer", e);
			}
		}
	}

}
