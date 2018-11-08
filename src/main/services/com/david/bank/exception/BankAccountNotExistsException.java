package com.david.bank.exception;

public class BankAccountNotExistsException extends RuntimeException {
	public BankAccountNotExistsException(String accountNumber) {
		super("bank account of account number='" + accountNumber + "' does not exists");
	}
}
