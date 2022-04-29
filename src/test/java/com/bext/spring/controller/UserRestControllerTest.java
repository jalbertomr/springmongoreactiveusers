package com.bext.spring.controller;


import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.BodySpec;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.web.reactive.function.BodyInserters;

import com.bext.spring.service.UserServiceImpl;
import com.bext.spring.user.domain.User;

import reactor.core.publisher.Flux;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;

@WebFluxTest({UserRestController.class, UserServiceImpl.class})
public class UserRestControllerTest {

	@Autowired
	private ApplicationContext context;
	private WebTestClient webTestClient;
	
	@BeforeEach
	public void setup() throws InterruptedException {
		webTestClient = WebTestClient.bindToApplicationContext(context).configureClient().baseUrl("/").build();
	}
	
	@Test
	@Order(1)
	public void getUserByIdFromInitialDataModel_ReturnsUser() throws Exception {
		ResponseSpec responseSpec = webTestClient.get().uri("/user/1").exchange();
     	responseSpec.expectStatus().isOk()
		.expectBody()
		.jsonPath("$.name").isNotEmpty()
		.jsonPath("$.name").isEqualTo("Daisy Ridley")
		.jsonPath("$.email").isEqualTo("daisy.ridley@email.com")
		.jsonPath("$.password").isEqualTo("daisypassword")
		.jsonPath("$.roles[0]").isEqualTo("ADMIN");
	}
	
	@Test
	@Order(2)
	public void getUserByIdFromInitialDataModel_ReturnsUser3() throws Exception {
		ResponseSpec responseSpec = webTestClient.get().uri("/user/3").exchange();
     	responseSpec.expectStatus().isOk()
		.expectBody()
		.jsonPath("$.name").isNotEmpty()
		.jsonPath("$.name").isEqualTo("Peter Parker")
		.jsonPath("$.email").isEqualTo("peter.parker@email.com")
		.jsonPath("$.password").isEqualTo("peterpassword")
		.jsonPath("$.roles[0]").isEqualTo("USER")
		.jsonPath("$.roles[1]").isEqualTo("ADMIN");
	}
	
	@Test
	@Disabled
	public void getUserByIdFromInitialDataModel_ReturnsUser2() throws Exception {
		ResponseSpec responseSpec = webTestClient.get().uri("/user/1").exchange();
		responseSpec.expectStatus().isOk()
		.expectBody(User.class)
		.consumeWith( result -> {
			 User user = result.getResponseBody();
			Assertions.assertThat(user).isNotNull();
			Assertions.assertThat(user.getName()).isEqualTo("Daisy Ridley");
		});
	}
	
	
	@Test
	@Order(3)
	public void getUserByIdFromInitialDataModel_NotFound() throws Exception {
		ResponseSpec responseSpec = webTestClient.get().uri("/user/-1").exchange();
		responseSpec.expectStatus().isNotFound();
	}
	
	
	@Test
	@Order(4)
	public void getUserById_invalid_error() throws Exception {
	  ResponseSpec responseSpec = webTestClient.get().uri("/user/-1").exchange();
	  responseSpec.expectStatus().isNotFound();
	}
	
	@Test
	@Order(5)
	public void createUserWithValidUserInputTest() {
		var user = new User(6, "Beto", "beto@test.com","secret", List.of("ADMIN","USER"), null, true );
		ResponseSpec responseSpec = webTestClient.post().uri("/user")
		.body(BodyInserters.fromValue(user))
		.exchange();
		
		responseSpec.expectStatus().isCreated()
		.expectHeader()
		.valueMatches("LOCATION", "^/user/\\d+")
		.expectBody()
		.jsonPath("$.id").isEqualTo(6)
		.jsonPath("$.name").isEqualTo("Beto")
		.jsonPath("$.email").isEqualTo("beto@test.com")
		.jsonPath("$.roles[0]").isEqualTo("ADMIN")
		.jsonPath("$.lastLogin").exists();
		//.expectBody()
		//.jsonPath("$", isSize(1));
	}
	
	@Test
	@Order(6)
	public void createUserMethodNotAllowed() {
		var user = new User(6,"Beto","beto@test.com","secret", List.of("USER"), null, true);
		webTestClient.post().uri("/user/search")
		.body(BodyInserters.fromValue(user))
		.exchange()
		.expectStatus().is4xxClientError();
	}
	
	@Test
	@Order(7)
	void deleteUserSuccess() {
		var user = new User(1, "Daisy Ridley", "daisy.ridley@email.com", "daisypassword", Collections.singletonList("ADMIN"), Instant.now(), true);
		webTestClient.delete().uri("/user/1").exchange()
		.expectStatus().isOk()
		.expectBody()
		.jsonPath("$.id").isEqualTo(1)
		.jsonPath("$.name").isEqualTo(user.getName())
		.jsonPath("$.email").isEqualTo(user.getEmail())
		.jsonPath("$.password").isEqualTo(user.getPassword())
		.jsonPath("$.roles[0]").isEqualTo("ADMIN");
	}
	
	@Test
	@Order(8)
	void deleteUserNotFound() {
		webTestClient.delete().uri("/user/10").exchange()
		.expectStatus().isNotFound();
	}
	
	@Test
	@Order(9)
	void deleteUserMethodNotAllowed() {
		webTestClient.delete().uri("/user")
		.exchange()
		.expectStatus().is4xxClientError();
	}
}
