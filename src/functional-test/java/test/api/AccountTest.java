package test.api;

import io.restassured.http.ContentType;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class AccountTest extends FunctionalTest {

	@Test
	public void testGetAllAccounts() {
		given().when().get("/api/account").then()
				.statusCode(200)
				.contentType(ContentType.JSON)
				.body("accountNumber", hasItems("a0001", "a0002", "a0003"));
	}

	@Test
	public void testGetSingleAccount() {
		given().when().get("/api/account/a0001").then()
				.statusCode(200)
				.contentType(ContentType.JSON)
				.body("accountNumber", equalTo("a0001"))
				.body("balance.amount", equalTo(120))
				.body("balance.currency", equalTo("GBP"))
		;
	}

	@Test
	public void testGetNonExistsAccount() {
		given().when().get("/api/account/a0004").then()
				.statusCode(404)
				.contentType(ContentType.JSON)
				.body("code", equalTo(404))
		;
	}
}



