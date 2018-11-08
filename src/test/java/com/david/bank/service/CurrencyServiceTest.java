package com.david.bank.service;

import com.david.bank.Money;
import com.david.bank.currency.CurrencyCode;
import com.david.bank.currency.CurrencyService;
import com.david.bank.exception.InvalidMoneyAmountException;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class CurrencyServiceTest {

	private CurrencyService	service = new CurrencyService();

	@Test
	public void testAddMoney() {
		Money a = new Money(12);
		Money b = new Money(18);
		assertEquals( "12+18 must be 30", 30, service.add(a, b).getAmount().intValue());

		Money c = new Money(new BigDecimal("32.17"), CurrencyCode.GBP);
		Money d = new Money(new BigDecimal("762.39"), CurrencyCode.GBP);
		assertEquals(new BigDecimal("794.56"), service.add(c, d).getAmount());
	}

	@Test
	public void testSubtractMoney() {
		Money a = new Money(12);
		Money b = new Money(18);
		assertEquals( "18-12 must be 6", 6, service.subtract(b, a).getAmount().intValue());

		Money c = new Money(new BigDecimal("1232.17"), CurrencyCode.GBP);
		Money d = new Money(new BigDecimal("762.39"), CurrencyCode.GBP);
		assertEquals(new BigDecimal("469.78"), service.subtract(c, d).getAmount());
	}

	@Test(expected = InvalidMoneyAmountException.class)
	public void testInvalidSubtract() {
		Money a = new Money(12);
		Money b = new Money(18);
		service.subtract(a, b).getAmount().intValue();
	}
}
