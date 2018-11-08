package com.david.bank;

import com.david.bank.currency.CurrencyService;
import com.david.bank.dao.BankAccountDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ManagedBean;
import javax.inject.Singleton;
import java.util.List;
import java.util.function.Function;

@ManagedBean
@Singleton
public class BankAccountService {
	private final static Logger logger = LogManager.getLogger(BankAccountService.class);

	public CurrencyService currencyService;

	public BankAccountDao bankAccountDao;


	public List<BankAccount> getAllBankAccounts() {
		return bankAccountDao.getAll();
	}

	public BankAccount getBankAccount(String accountNumber) {
		return bankAccountDao.get(accountNumber);
	}

	/**
	 * deposit money into an account
	 * in practice must be transactional
	 * @param account the target bank account to put money in
	 * @param money the money to deposit
	 * @return the target bank account
	 */
	public BankAccount deposit(BankAccount account, Money money) {
		logger.debug("deposit "+money+" to account " + account);

		Money beforeBalance = account.getBalance();
		Money afterBalance = currencyService.add(beforeBalance, money);
		account.setBalance(afterBalance);

		return account;
	}


	/**
	 * withdrawal money into an account
	 * in practice must be transactional
	 * @param account the target bank account to take money from
	 * @param money the money to withdrawal
	 * @return the target bank account
	 */
	public BankAccount withdrawal(BankAccount account, Money money) {

		Function<Money, Boolean> checkWithdrawal = (m) -> {
			if (false == money.isPositive()) {
				throw new WithdrawalException(account, m);
			}
			return true;
		};

		logger.debug("withdrawal "+money+" from account " + account);

		Money beforeBalance = account.getBalance();
		checkWithdrawal.apply(beforeBalance);

		Money afterBalance = currencyService.subtract(beforeBalance, money);
		checkWithdrawal.apply(afterBalance);


		account.setBalance(afterBalance);

		return account;
	}


	//--------------------------------------------------
	//exceptions
	public class WithdrawalException extends RuntimeException {
		public WithdrawalException(BankAccount account, Money money) {
			super("invalid withdrawal from account;" + account + ", money:" + money);
		}
	}

}
