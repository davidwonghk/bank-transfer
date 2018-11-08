package com.david.bank.currency;

import com.david.bank.Money;
import com.david.bank.exception.InvalidMoneyAmountException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.annotation.ManagedBean;
import javax.inject.Singleton;
import java.math.BigDecimal;

/**
 * To keep the simplicity of the demo,
 * note that the currencyService is a mock here for demo purpose.
 *
 * In real world currencyService should be provided by another micro-service (Restful API)
 */
@ManagedBean
@Singleton
public class CurrencyService {


	public Money subtract(Money src, Money other) {
		checkNotImplemented(src.getCurrency(), other.getCurrency());

		//for subtraction, there should be some money for src to subtract
		checkMoney(src);
		//and this is meaningless to subtract zero
		checkMoney(other);

		BigDecimal amount = src.getAmount().subtract(other.getAmount());
		if (amount.compareTo(BigDecimal.ZERO) < 0) {
			throw new InvalidMoneyAmountException("money subtracted to negative. src:"+src+", other:"+other);
		}

		return new Money(amount, src.getCurrency());
	}

	public Money add(Money src, Money other) {
		checkNotImplemented(src.getCurrency(), other.getCurrency());

		checkMoney(other);

		BigDecimal amount = src.getAmount().add(other.getAmount());
		return new Money(amount, src.getCurrency());
	}


	public boolean isCurrencySupport(CurrencyCode currencyCode) {
		if (currencyCode != CurrencyCode.GBP) {
			return false;
		}
		return true;
	}


	private boolean checkMoney(Money money) {
		if (money.isPositive()) return true;
		throw new InvalidMoneyAmountException(money);
	}

	private void checkNotImplemented(CurrencyCode src, CurrencyCode target) {
		//for demo assume all money are at same currency
		if (src != target) {
			throw new NotImplementedException();
		}
	}


}
