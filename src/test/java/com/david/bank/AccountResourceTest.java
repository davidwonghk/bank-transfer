package com.david.bank;

import com.david.bank.currency.CurrencyService;
import com.david.bank.dao.BankAccountDao;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.inject.Inject;
import javax.ws.rs.core.Application;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Ignore	//TODO: not familiar with JerseyTest yet
public class AccountResourceTest extends JerseyTest {
	@Inject
	private BankAccountService bankAccountService;

	private BankAccountDao bankAccountDao;
	private CurrencyService currencyService;

	@Override
	protected Application configure() {
		return new ResourceConfig(AccountResource.class);
	}

	@Before
	public void setUp() {

		this.currencyService = new CurrencyService();
		this.bankAccountDao = new BankAccountDao();
		this.bankAccountService = new BankAccountService();
		bankAccountService.bankAccountDao = bankAccountDao;

		ServiceLocator locator = ServiceLocatorUtilities.bind(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(currencyService).to(CurrencyService.class);
				bind(bankAccountDao).to(BankAccountDao.class);
				bind(bankAccountService).to(BankAccountService.class);
			}
		});
		locator.inject(this);
	}

	@Test
	public void testAccountIndex() {
		List<BankAccount> accounts = target("api/account/").request().get(List.class);
		assertEquals(bankAccountDao.getAll(), accounts);
	}
}
