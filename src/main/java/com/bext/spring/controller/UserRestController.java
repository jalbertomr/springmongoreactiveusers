package com.bext.spring.controller;

import java.net.URI;
import java.time.Instant;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.bext.spring.repo.IUserRepository;
import com.bext.spring.service.IUserService;
import com.bext.spring.user.domain.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
public class UserRestController {

	@Autowired
	private IUserRepository userRepository;
	
	@Autowired
	private IUserService userService;

//	@GetMapping
//	public Flux<User> getUsers(){
//		return userRepository.findAll();
//	}
	
	@GetMapping
	public Flux<?> getUsersLimit(@RequestParam(name = "limit", required = false, defaultValue = "-1") long limit) {
		return userService.findAllLimit(limit);
	}

	@GetMapping("/{id}")
	public Mono<ResponseEntity<User>> getUserById(@PathVariable("id") long Id) {
		Mono<User> monoUser = userService.findById(Id);
		return monoUser.map(ResponseEntity::ok)
			//	.switchIfEmpty(Mono.error((new ResponseStatusException(HttpStatus.NOT_FOUND)))); //generates a stack trace
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@GetMapping("/email/{email}")
	public Flux<?> getUserByEmailIgnoreCase(@PathVariable("email") String email) {
		return userService.findByEmailContainingIgnoreCase(email);
				//.switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
	}

	@PostMapping
	public Mono<ResponseEntity<User>> newUserMono(@Valid @RequestBody Mono<User> userMono, ServerHttpRequest req) {
		return userMono.flatMap(userService::save)
					.map(u -> ResponseEntity.created(URI.create(req.getPath() + "/" + u.getId())).body(u));
	}

	@PostMapping("/search")
	public Mono<ResponseEntity<User>> getUserByExample(@RequestBody Mono<User> userMono) {
		return userMono.flatMap(user -> userService.findUserbyExample(user))
				.map(u -> ResponseEntity.ok(u))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

//	@DeleteMapping("/{id}")
//	public Mono<ResponseEntity<Void>> deleteUser(@PathVariable("id") long id) {
//		return userService.deleteWithHttpResponse(id);
//	}
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<User>> deleteUser(@PathVariable long id) {
		return userService.deleteById(id)
				.map(userDeleted -> ResponseEntity.ok(userDeleted))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
		        //.switchIfEmpty(Mono.error( new ResponseStatusException(HttpStatus.NOT_FOUND))); //same but with stacktrace
	}
}
