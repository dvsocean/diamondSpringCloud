package com.example;

import com.example.MvcTest;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import io.restassured.response.ResponseOptions;
import org.junit.Test;

import static com.toomuchcoding.jsonassert.JsonAssertion.assertThatJson;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static org.springframework.cloud.contract.verifier.assertion.SpringCloudContractAssertions.assertThat;

public class ContractVerifierTest extends MvcTest {

	@Test
	public void validate_shouldMarkClientAsFraud() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/vnd.fraud.v1+json")
					.body("{\"clientId\":\"1234567890\",\"loanAmount\":99999}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/fraudcheck");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/vnd.fraud.v1+json");
		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['rejectionReason']").isEqualTo("Amount too high");
			assertThatJson(parsedJson).field("['fraudCheckStatus']").isEqualTo("FRAUD");
	}

}
