package com.david.bank.service;

import com.david.bank.BankAccount;
import com.david.bank.BankAccountService;
import com.david.bank.Money;
import com.david.bank.currency.CurrencyService;
import com.david.bank.dao.BankAccountDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class BankAccountServiceTest {
	BankAccountService service = new BankAccountService();


	@Before
	public void setup() {
		service.currencyService = Mockito.mock(CurrencyService.class);
		service.bankAccountDao = Mockito.mock(BankAccountDao.class);
	}

	@Test
	public void testDeposit() {
		Money m1 = new Money(10);
		Money m2 = new Money(2);
		BankAccount ac1 = new BankAccount("a1");
		ac1.setBalance(m1);

		when(service.currencyService.add(m1, m2)).thenReturn(new Money(12));
		service.deposit(ac1, m2);

		assertEquals(12,
				ac1.getBalance()
				.getAmount()
				.intValue());
	}

	@Test
	public void testWithdrawal() {
		Money m1 = new Money(10);
		Money m2 = new Money(2);
		BankAccount ac1 = new BankAccount("a1");
		ac1.setBalance(m1);

		when(service.currencyService.subtract(m1, m2)).thenReturn(new Money(8));
		service.withdrawal(ac1, m2);

		assertEquals(8,
				ac1.getBalance()
				.getAmount()
				.intValue());
	}


	@Test
	public void testGetAllBankAccounts() {
		Money m1 = new Money(7);
		BankAccount ac1 = new BankAccount("a1");
		ac1.setBalance(m1);

		Money m2 = new Money(32);
		BankAccount ac2 = new BankAccount("a2");
		ac2.setBalance(m2);

		List<BankAccount> list = new ArrayList<>();
		list.add(ac1);
		list.add(ac2);

		when(service.bankAccountDao.getAll()).thenReturn(list);

		assertEquals(list, service.getAllBankAccounts());
	}

	@Test
	public void testGetBankAccount() {
		//TODO
	}
}
