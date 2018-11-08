package com.david.bank.transaction;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("api/transaction")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransactionResource {
	@Inject
	public TransactionService transactionService;


	/**
	 * @return transaction history (ie. all processed transaction)
	 */
	@GET
	public List<PersistedTransaction> list() {
		return transactionService.getAllTransactionHistory();
	}

	/**
	 * @param transactionNumber unique identifier of transaction to query
	 * @return transaction history (ie. all processed transaction)
	 */
	@GET
	@Path("{id}")
	public PersistedTransaction item(@PathParam("id") String transactionNumber) {
		return transactionService.getTransaction(transactionNumber);
	}

	/**
	 * @param command json request body for the transaction
	 * @return a pending money transfer transaction for the request
	 */
	@POST
	public TransferTransaction transfer(@Valid TransferCommand command) {
		TransferTransaction transaction = command.toTransferTransaction();
		if (transactionService.validateTransaction(transaction)) {
			return transactionService.requestTransfer(transaction);
		}

		//if validateTransaction method fail,
		//there should be exception to be thrown at this point
		return null;
	}





}
