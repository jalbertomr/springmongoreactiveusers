package com.bext.spring.controller;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
	public Mono<ResponseEntity<User>> getUserById(@PathVariable("id") long id){
		Mono<User> monoUser = Mono.from(users.filter( user -> id == user.getId()));
		return monoUser.map( ResponseEntity::ok)
				.switchIfEmpty( Mono.error(( new ResponseStatusException(HttpStatus.NOT_FOUND) )));
	}
/*	
	@PostMapping
	public Mono<User> newUser(@RequestBody User user){
		Mono<User> monoUser = Mono.just(user);
		users = users.mergeWith(monoUser);
		return monoUser;
	}
*/	
	@PostMapping     //For Test
	public Mono<ResponseEntity<Object>> newUser(@RequestBody Mono<User> userMono, ServerHttpRequest req){
		userMono = userMono.map( user -> {
			user.setId(6);
			return user;
		});
		users = users.mergeWith(userMono);
		return userMono.map( u -> 
			ResponseEntity.created(URI.create(req.getPath() + "/" + u.getId())).build());
	}
	
	@DeleteMapping("/{id}")
	public Mono<Void> deleteUser(@PathVariable("id") long id){
		users = users.filter(user -> user.getId() != id);
		return users.then();
	}
	
}
