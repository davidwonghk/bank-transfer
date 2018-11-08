package com.david.bank.exception;

import com.david.bank.Money;

public class InvalidMoneyAmountException extends RuntimeException  {
	public InvalidMoneyAmountException(Money money) {
		super("Invalid money amount");
	}

	public InvalidMoneyAmountException(String message) {
		super(message);
	}
}
