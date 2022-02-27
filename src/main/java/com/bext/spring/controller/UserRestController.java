package com.bext.spring.controller;

import java.time.Instant;
import java.util.Collections;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bext.spring.user.domain.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
public class UserRestController {
 
	private Flux<User> users;
	
	public UserRestController() {
		users = createUsersModel();
	}

	private Flux<User> createUsersModel() {
		User user = new User(1, "Daisy Ridley", "daisy.ridley@email.com", "daisypassword", Collections.singletonList("ADMIN"), Instant.now(), true);
		User user2= new User(2,"Tom Raider","tom.raider@email.com","tompassword", Collections.singletonList("ADMIN"), Instant.now(), false);
		User user3= new User(3,"Peter Parker","peter.parker@email.com", "peterpassword", Collections.singletonList("USER"), Instant.now(), true);
		return Flux.just( user, user2, user3);
	}
	
	@GetMapping
	public Flux<User> getUsers(@RequestParam(name = "limit", required = false, defaultValue = "-1") long limit){
		if (limit == -1) {
			return users;
		}
		return users.take(limit);
	}
	
	@GetMapping("/{id}")
	public Mono<User> getUserById(@PathVariable("id") long id){
		return Mono.from(users.filter( user -> id == user.getId()));
	}
}
