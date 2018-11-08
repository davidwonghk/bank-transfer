package com.david.bank;

import com.david.bank.BankAccount;
import com.david.bank.BankAccountService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;


/**
 * bank account RESTFul API Controller
 */
@Path("api/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {
	//TODO: entrypoint
	//TODO: pagination

	@Inject
	public BankAccountService bankAccountService;

	/**
	 * @return all accounts in the system
	 */
	@GET
	public List<BankAccount> list() {
		return bankAccountService.getAllBankAccounts();
	}


	/**
	 * @param accountNumber the unique identifier of account
	 * @return specific bank account by accountNumber
	 */
	@GET
	@Path("{id}")
	public BankAccount item(@PathParam("id") String accountNumber) {
		return bankAccountService.getBankAccount(accountNumber);
	}


}
