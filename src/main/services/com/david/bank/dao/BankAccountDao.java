package com.david.bank.dao;

import com.david.bank.BankAccount;
import com.david.bank.Money;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class BankAccountDao implements Dao<BankAccount, String> {

	//protected for unit test
	private static Map<String, BankAccount> accounts = new ConcurrentHashMap<String, BankAccount>();


	public BankAccount get(String accountNumber) throws EntityNotFoundException {
		BankAccount bankAccount = accounts.get(accountNumber);
		if (null == bankAccount) {
			throw new EntityNotFoundException(BankAccount.class, accountNumber);
		}

		return bankAccount;
	}

	public List<BankAccount> getAll() {
		return new ArrayList<BankAccount>(accounts.values());
	}

	public BankAccount save(BankAccount bankAccount) {
		assert (bankAccount != null);

		final String accountNumber = bankAccount.getAccountNumber();
		accounts.put(accountNumber, bankAccount);
		return bankAccount;
	}

	public void delete(BankAccount bankAccount) throws EntityNotFoundException {
		final String accountNumber = bankAccount.getAccountNumber();

		BankAccount removed = accounts.remove(accountNumber);
		if (null == removed) {
			throw new EntityNotFoundException(getClass(), accountNumber);
		}

	}


	/**
	 * mock method to simulate locking dao
	 */
	public void lock(BankAccount account) { }

	/**
	 * mock method to simulate unlocking dao
	 */
	public void unlock(BankAccount account) { }

	//mock data for the demo
	static {
		BankAccount ac1 = new BankAccount("a0001");
		ac1.setBalance(new Money(120));
		accounts.put(ac1.getAccountNumber(), ac1);

		BankAccount ac2 = new BankAccount("a0002");
		ac2.setBalance(new Money(3000));
		accounts.put(ac2.getAccountNumber(), ac2);

		BankAccount ac3 = new BankAccount("a0003");
		ac3.setBalance(new Money(0));
		accounts.put(ac3.getAccountNumber(), ac3);
	}
}
