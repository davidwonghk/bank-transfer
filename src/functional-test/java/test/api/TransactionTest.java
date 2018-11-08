package test.api;

import io.restassured.http.ContentType;
import org.junit.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;



public class TransactionTest  extends FunctionalTest {
	@Test
	public void testTransferMoneyHappyCase() {

		String payload = "{ \"senderAccountNumber\": \"a0002\", \"receiverAccountNumber\": \"a0001\", \"currency\": \"GBP\", \"amount\": \"12.7\", \"description\": \"hi\" }";

		given()
				.contentType(ContentType.JSON)
				.body(payload)
			.when()
				.post("/api/transaction")
			.then()
				.statusCode(200)
				.contentType(ContentType.JSON)
				.body("senderAccountNumber", equalTo("a0002"))
				.body("receiverAccountNumber", equalTo("a0001"))
				.body("description", equalTo("hi"))
				.body("status", equalTo("PENDING"))
				.body("money.currency", equalTo("GBP"))
			;
	}


	@Test
	public void testTransferMoneyMoreThanSaving() {

		String payload = "{ \"senderAccountNumber\": \"a0002\", \"receiverAccountNumber\": \"a0001\", \"currency\": \"GBP\", \"amount\": \"9999\"}";

		given()
				.contentType(ContentType.JSON)
				.body(payload)
			.when()
				.post("/api/transaction")
			.then()
				.statusCode(400)
				.contentType(ContentType.JSON)
		;
	}

	@Test
	public void testTransferMoneyToNonExistsAccount() {

		String payload = "{ \"senderAccountNumber\": \"a0004\", \"receiverAccountNumber\": \"a0001\", \"currency\": \"GBP\", \"amount\": \"9\"}";

		given()
				.contentType(ContentType.JSON)
				.body(payload)
				.when()
				.post("/api/transaction")
				.then()
				.statusCode(404)
				.contentType(ContentType.JSON)
		;
	}

	@Test
	public void testTransferMoneyMissingReceiverNumber() {

		String payload = "{ \"senderAccountNumber\": \"a0004\", \"currency\": \"GBP\", \"amount\": \"9\"}";

		given()
				.contentType(ContentType.JSON)
				.body(payload)
				.when()
				.post("/api/transaction")
				.then()
				.statusCode(400)
		;
	}

	@Test
	public void testListAllTransactions() {
		given()
			.when()
				.get("/api/transaction")
			.then()
				.statusCode(200)
				.contentType(ContentType.JSON)
		;

	}


	@Test
	public void testGetNonExistTransaction() {
		given()
			.when()
				.get("/api/transaction/abc123")
			.then()
				.statusCode(404)
				.contentType(ContentType.JSON)
		;

	}
}
