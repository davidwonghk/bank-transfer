package com.david.bank.transaction;


import com.david.bank.Money;
import com.david.bank.currency.CurrencyCode;
import com.david.bank.exception.CurrencyNotSupportException;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.beans.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * command object for the transaction API for input the detail of a requesting money transfer transaction
 * including fields that are necessary for the sender to fill
 */
public class TransferCommand implements Serializable {
	@NotNull
	private String senderAccountNumber;

	@NotNull
	private String receiverAccountNumber;

	@NotNull
	private String currency;

	@NotNull
	@DecimalMin(value = "0", message = "Transfer amount should be greater than zero")
	private String amount;

	private String description;

	public void setSenderAccountNumber(String senderAccountNumber) {
		this.senderAccountNumber = senderAccountNumber;
	}

	public void setReceiverAccountNumber(String receiverAccountNumber) {
		this.receiverAccountNumber = receiverAccountNumber;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	//force external to use string represent amount as for accuracy
	public void setAmount(String amount) {
		this.amount = amount;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Transient
	public TransferTransaction toTransferTransaction() {
		return new TransferTransaction() {
			@Override public Date getRequestedTime()           { return null; }
			@Override public String getTransactionNumber()     { return null; }
			@Override public String getSenderAccountNumber()   { return senderAccountNumber; }
			@Override public String getReceiverAccountNumber() { return receiverAccountNumber; }
			@Override public String getDescription()           { return description; }
			@Override public TransactionStatus getStatus()     { return TransactionStatus.PENDING; }

			@Override public Money getMoney() {
				BigDecimal value = new BigDecimal(amount);
				if (!currency.equals(CurrencyCode.GBP.name())) {
					throw new CurrencyNotSupportException(currency);
				}
				CurrencyCode currencyCode = CurrencyCode.valueOf(currency);
				return new Money(value, currencyCode);
			}

		};
	}
}

