package com.bext.spring.controller;

import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.web.reactive.function.BodyInserters;

import com.bext.spring.service.IUserService;
import com.bext.spring.user.domain.User;

import reactor.core.publisher.Mono;

@WebFluxTest(UserRestController.class)
@TestMethodOrder(OrderAnnotation.class)
public class UserRestControllerMockedTest {

	@MockBean
	private IUserService userService;

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
		// setup mock
		long id = 1;
		String name = "Daisy Ridley";

		when(userService.findById(id)).thenReturn(Mono.just(User.builder().name(name).build()));

		ResponseSpec responseSpec = webTestClient.get().uri("/user/1").exchange();
		responseSpec.expectStatus().isOk().expectBody().jsonPath("$.name").isNotEmpty().jsonPath("$.name")
				.isEqualTo("Daisy Ridley");
	}

	@Test
	@Order(2)
	public void getUserByIdFromInitialDataModel_ReturnsUser3() throws Exception {
		long id = 3;
		String name = "Peter Parker";
		when(userService.findById(id)).thenReturn(Mono.just(User.builder().name(name).build()));

		ResponseSpec responseSpec = webTestClient.get().uri("/user/3").exchange();
		responseSpec.expectStatus().isOk().expectBody().jsonPath("$.name").isNotEmpty().jsonPath("$.name")
				.isEqualTo("Peter Parker");
	}

	@Test
	@Order(3)
	public void getUserByIdFromInitialDataModel_NotFound() throws Exception {
		long idNotExist = 100;
		when(userService.findById(idNotExist)).thenReturn(Mono.empty());

		ResponseSpec responseSpec = webTestClient.get().uri("/user/" + idNotExist).exchange();
		responseSpec.expectStatus().isNotFound();
	}

	@Test
	@Order(4)
	public void getUserById_invalid_error() throws Exception {
		long idNotExist = -1;
		when(userService.findById(idNotExist)).thenReturn(Mono.empty());
		ResponseSpec responseSpec = webTestClient.get().uri("/user/-1").exchange();
		responseSpec.expectStatus().isNotFound();
	}

	@Test
	@Order(5)
	public void createUserWithValidUserInputTest() {
		var user = new User(6, "Beto", "beto@test.com", "secret", List.of("ADMIN", "USER"), Instant.now(), true);
		Mockito.when(userService.save(ArgumentMatchers.any())).thenReturn(Mono.just(user));

		ResponseSpec responseSpec = webTestClient.post().uri("/user").body(BodyInserters.fromValue(user)).exchange();

		responseSpec.expectStatus().isCreated().expectHeader().valueMatches("LOCATION", "^/user/\\d+").expectBody()
				.jsonPath("$.id").isEqualTo(6).jsonPath("$.name").isEqualTo("Beto").jsonPath("$.email")
				.isEqualTo("beto@test.com").jsonPath("$.roles[0]").isEqualTo("ADMIN").jsonPath("$.lastLogin").exists();

		// .expectBody()
		// .jsonPath("$", isSize(1));
	}

	@Test
	@Order(6)
	public void createUserMethodNotAllowed() {
		var user = new User(6, "Beto", "beto@test.com", "secret", List.of("USER"), null, true);
		webTestClient.post().uri("/user/search").body(BodyInserters.fromValue(user)).exchange().expectStatus()
				.is4xxClientError();
	}

	@Test
	@Order(7)
	void deleteUserSuccess() {
		User user = new User(1, "Daisy Ridley", "daisy.ridley@email.com", "daisypassword",
				Collections.singletonList("ADMIN"), Instant.now(), true);
		Mockito.when(userService.deleteById(1L)).thenReturn(Mono.just(user));

		webTestClient.delete().uri("/user/1").exchange().expectStatus().isOk().expectBody().jsonPath("$.id")
				.isEqualTo(1).jsonPath("$.name").isEqualTo(user.getName()).jsonPath("$.email")
				.isEqualTo(user.getEmail()).jsonPath("$.password").isEqualTo(user.getPassword()).jsonPath("$.roles[0]")
				.isEqualTo("ADMIN");
	}

	@Test
	@Order(8)
	void deleteUserNotFound() {
		long id = 100;
		when(userService.deleteById(id)).thenReturn(Mono.empty());

		webTestClient.delete().uri("/user/100").exchange().expectStatus().isNotFound();
	}

	@Test
	@Order(9)
	void deleteUserMethodNotAllowed() {
		webTestClient.delete().uri("/user").exchange().expectStatus().is4xxClientError();
	}

}
