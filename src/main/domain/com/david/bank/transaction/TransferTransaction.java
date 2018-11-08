package com.david.bank.transaction;

import com.david.bank.Money;

import javax.validation.constraints.Size;
import java.util.Date;


public interface TransferTransaction {
	@Size(min = 8, max = 8)
	String getSenderAccountNumber();

	@Size(min = 8, max = 8)
	String getReceiverAccountNumber();

	//the datetime of the transaction when it is first requested
	Date getRequestedTime();

	//transaction value amount on the currency stated above
	Money getMoney();

	@Size(min = 18, max = 18)
	//Identity of the transaction
	String getTransactionNumber();

	TransactionStatus getStatus();

	//description about the transaction wrote by the sender
	String getDescription();
}
