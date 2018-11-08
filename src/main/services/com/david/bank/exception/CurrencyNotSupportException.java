package com.david.bank.exception;

import com.david.bank.currency.CurrencyCode;

public class CurrencyNotSupportException extends RuntimeException {
	public CurrencyNotSupportException(String currency) {
		super("Currency " + currency + " is currently not supported");
	}
}
