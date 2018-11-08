package com.david.bank.transaction;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * queue for money transfer transaction using by producer-consumer model
 * for ensuring the transactional between bank accounts,
 * since TransferTransaction can only be parsed one by one, and distribute to different consumer
 * base on the corresponding bank accounts(sender + receiver)
 *
 * nt. this is an in-memory mock only
 * in practice this should be a queue system (eg. ActiveMQ, RabbitMQ) with redundancy and failover support
 */
public class PendingTransactionQueue extends ConcurrentLinkedQueue<PersistedTransaction> {

	 private static PendingTransactionQueue instance;

	 private static ReentrantLock lock = new ReentrantLock();

	 //singleton
	 public static PendingTransactionQueue getInstance() {
	 	//Double-Checked Locking for lazy initialization
	 	if (instance == null) {
			lock.lock();
			try{
				if (instance == null) {
					instance = new PendingTransactionQueue();
				}
			} finally {
				lock.unlock();
			}
		}
		return instance;
	 }

}
