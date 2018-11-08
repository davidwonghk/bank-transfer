package com.david.bank;


import com.david.bank.dao.Dao;
import com.david.bank.exception.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.http.util.HttpStatus;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.Serializable;

/**
 * Exception handle layers to catch all the exception throws out application
 * and response a proper http status code
 */
@Provider
public class BankExceptionMapper implements ExceptionMapper<Throwable> {
	private final static Logger logger = LogManager.getLogger(BankExceptionMapper.class);

	/**
	 * for the output template of Api error
	 * eg. * {"message":"Entity a of BankAccount is not found", "code":404}
	 */
	public static class ErrorMessage implements Serializable {
		private String message;
		private int code;

		public ErrorMessage(Throwable e, int code) {
			this.message = e.getMessage();
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public int getCode() {
			return code;
		}
	}


	@Override
	public Response toResponse(Throwable t) {
		logger.error(t.getMessage(), t);
		t.printStackTrace();

		int status = getStatusCode(t);
		ErrorMessage errorMessage = new ErrorMessage(t, status);

		return Response
				.status(status)
				.entity(errorMessage)
				.build();
	}


	protected int getStatusCode(Throwable e) {
		//expend the code below to add new Exception to hanlde
		if (e instanceof Dao.EntityNotFoundException) {
			return HttpStatus.NOT_FOUND_404.getStatusCode();
		}

		if (e instanceof BankAccountNotExistsException) {
			return HttpStatus.NOT_FOUND_404.getStatusCode();
		}

		if (e instanceof CurrencyNotSupportException) {
			return 422;	//Unprocessable Entity
		}

		if (e instanceof InvalidMoneyAmountException) {
			return HttpStatus.BAD_REQUEST_400.getStatusCode();
		}

		if (e instanceof NotImplementedException) {
			return HttpStatus.NOT_IMPLEMENTED_501.getStatusCode();
		}

		//default error handling
		return HttpStatus.INTERNAL_SERVER_ERROR_500.getStatusCode();
	}
}
