package com.david.bank;

import com.david.bank.currency.CurrencyCode;

import java.beans.Transient;
import java.io.Serializable;
import java.math.BigDecimal;

public class Money implements Serializable {
	private BigDecimal amount;
	private CurrencyCode currency;


	public Money(int amount) {
		this(new BigDecimal(amount), CurrencyCode.GBP);
	}

	public Money(BigDecimal amount, CurrencyCode currency) {
		this.currency = currency;
		this.amount = amount;
	}

	public Money(Money src) {
		this(src.amount, src.currency);
	}


	@Transient
	public boolean isPositive() {
		return amount.compareTo(BigDecimal.ZERO) > 0;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public CurrencyCode getCurrency() {
		return currency;
	}


	@Override
	public String toString() {
		return amount.toString();
	}
}

