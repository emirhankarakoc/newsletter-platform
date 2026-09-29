package com.karakoc.security_demosu;

import com.karakoc.enewsletter.ENewsletterApplication;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(classes = ENewsletterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ENewsletterApplicationTests {



	@Test
	 void testHelloWorld() {
		RestAssured.baseURI = "http://localhost:8080"; // Uygulamanızın çalıştığı port

		given()
				.when()
				.get("/hello/test-endpoint")
				.then()
				.statusCode(200)
				.body(equalTo("HELLO WORLD!"));
	}
}
