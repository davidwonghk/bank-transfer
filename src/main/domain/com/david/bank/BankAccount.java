package com.david.bank;

import com.david.bank.currency.CurrencyCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * representation of the Bank Account business object
 */
public class BankAccount implements Serializable {
	private String accountNumber;
	private Money balance;

	public BankAccount(String accountNumber) {
		this.accountNumber = accountNumber;
		this.balance = new Money(BigDecimal.ZERO, CurrencyCode.GBP);
	}


	//--------------------------------------------------
	//getters and setters

	public String getAccountNumber() {
		return accountNumber;
	}

	public Money getBalance() {
		return balance;
	}

	public void setBalance(Money balance) {
		this.balance = balance;
	}


	@Override
	public String toString() {
		return this.accountNumber;
	}

}
