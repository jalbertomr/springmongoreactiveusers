package com.bext.spring.controller;


import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.web.reactive.function.BodyInserters;

import com.bext.spring.user.domain.User;

@WebFluxTest(UserRestController.class)
public class UserRestControllerTest {

	@Autowired
	private ApplicationContext context;
	private WebTestClient webTestClient;
	
	@BeforeEach
	public void setup() {
		webTestClient = WebTestClient.bindToApplicationContext(context).configureClient().baseUrl("/").build();
	}
	
	@Test
	public void getUserByIdFromInitialDataModel_ReturnsUser() throws Exception {
		ResponseSpec responseSpec = webTestClient.get().uri("/user/1").exchange();
		responseSpec.expectStatus().isOk()
		
		//System.out.println("responseSpec.returnResult(User.class):" + responseSpec.returnResult(User.class));
		//responseSpec
		.expectBody(User.class)
		.consumeWith( result -> {
			User user = result.getResponseBody();
			Assertions.assertThat(user).isNotNull();
			Assertions.assertThat(user.getName()).isEqualTo("Daisy Ridley");
		});
	}
	
	@Test
	public void getUserByIdFromInitialDataModel_NotFound() throws Exception {
		ResponseSpec responseSpec = webTestClient.get().uri("/user/-1").exchange();
		responseSpec.expectStatus().isNotFound();
	}
	
	
	@Test
	public void getUserById_invalid_error() throws Exception {
	  ResponseSpec responseSpec = webTestClient.get().uri("/user/-1").exchange();
	  
	  responseSpec.expectStatus().isNotFound();
	}
	
	@Test
	public void createUserWithValidUserInputTest() {
		var user = new User(4, "Beto", "beto@test.com","secret", List.of("ADMIN","USER"), null, true );
		ResponseSpec responseSpec = webTestClient.post().uri("/user")
		.body(BodyInserters.fromValue(user))
		.exchange();
		
		responseSpec.expectStatus().isCreated().expectHeader()
		.valueMatches("LOCATION", "^/user/\\d+");
	}
}
