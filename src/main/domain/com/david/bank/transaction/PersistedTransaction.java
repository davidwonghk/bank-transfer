package com.david.bank.transaction;

import com.david.bank.currency.CurrencyCode;
import com.david.bank.Money;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * Money transfer transaction that is processed
 */
public class PersistedTransaction implements Serializable, TransferTransaction {

	//unique identifier of transaction
	protected String transactionNumber;

	//the datetime of the transaction when it is first requested
	protected Date requestedTime;

	protected String senderAccountNumber;
	protected String receiverAccountNumber;

	//the exchange rate agreed by the sender party at requestedTime
	protected CurrencyCode currency;

	//transaction value amount on the currency stated above
	protected BigDecimal amount;

	//description set by the sender
	protected String description;

	protected TransactionStatus status = TransactionStatus.PENDING;

	//the datetime of the transaction when it is processed
	// we hope processedTime-requestedTime to be minimal
	private Date processedTime;


	//for internal use
	private String failedReason;



	public PersistedTransaction(TransferTransaction t, String transactionNumber, Date requestedTime) {
		this.transactionNumber = t.getTransactionNumber();
		this.senderAccountNumber = t.getSenderAccountNumber();
		this.receiverAccountNumber = t.getReceiverAccountNumber();
		this.description = t.getDescription();
		this.setMoney(t.getMoney());

		assert(null == t.getTransactionNumber());
		assert(null == t.getRequestedTime());
		this.transactionNumber = transactionNumber;
		this.requestedTime = requestedTime;
	}


	@Override
	public String toString() {
		return getTransactionNumber();
	}



	//--------------------------------------------------
	//getters and setters

	public String getTransactionNumber() {
		return transactionNumber;
	}

	public Date getRequestedTime() {
		return requestedTime;
	}

	public String getSenderAccountNumber() {
		return senderAccountNumber;
	}

	public String getReceiverAccountNumber() {
		return receiverAccountNumber;
	}

	public Money getMoney() {
		return new Money(amount, currency);
	}

	public void setMoney(CurrencyCode currency, BigDecimal amount) {
		this.currency = currency;
		this.amount = amount;
	}

	public void setMoney(Money money) {
		this.currency = money.getCurrency();
		this.amount = money.getAmount();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	public String getFailedReason() {
		return failedReason;
	}

	public void setFailedReason(String failedReason) {
		this.failedReason = failedReason;
	}

	public Date getProcessedTime() {
		return processedTime;
	}

	public void setProcessedTime(Date processedTime) {
		this.processedTime = processedTime;
	}
}
