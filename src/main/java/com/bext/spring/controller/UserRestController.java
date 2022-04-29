package com.bext.spring.controller;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

import com.bext.spring.service.UserServiceImpl;
import com.bext.spring.user.domain.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
public class UserRestController {
 
	@Autowired
	private UserServiceImpl userService;
	
	@GetMapping
	public Flux<User> getUsers(@RequestParam(name = "limit", required = false, defaultValue = "-1") long limit){
		return userService.findAllLimit(limit);
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<User>> getUserById(@PathVariable("id") long Id){
		Mono<User> monoUser = userService.findById(Id);
		return monoUser.map( ResponseEntity::ok)
				.switchIfEmpty( Mono.error(( new ResponseStatusException(HttpStatus.NOT_FOUND) )));
	}

	@PostMapping
	public Mono<ResponseEntity<User>> newUser(@RequestBody User user, ServerHttpRequest req){
		user.setLastLogin(Instant.now());
		Mono<User> monoUser = Mono.just(user);
		userService.save(monoUser);
		//return monoUser.map( u -> ResponseEntity.created(URI.create(req.getPath() + "/" + u.getId())).build());
		//return monoUser.map( u -> new ResponseEntity<User>(u, new HttpHeaders(),HttpStatus.OK));
		return monoUser.map( u -> ResponseEntity.created( URI.create(req.getPath() + "/" + u.getId()) ).body(u)
				           );
	}
	
/*	
	@PostMapping     //Assign an fixed Id to test
	public Mono<ResponseEntity<Object>> newUserFixId(@RequestBody Mono<User> userMono, ServerHttpRequest req){
		userMono = userMono.map( user -> {
			user.setId(6);
			return user;
		});
		users = users.mergeWith(userMono);
		return userMono.map( u -> 
			ResponseEntity.created(URI.create(req.getPath() + "/" + u.getId()) ).body(u));
	}
*/
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<User>> deleteUser(@PathVariable("id") long id){
		Mono<User> userfound = userService.findById(id);
		userService.deleteById(id);
		return userfound.map(ResponseEntity::ok)
				.switchIfEmpty( Mono.error((new ResponseStatusException(HttpStatus.NOT_FOUND))));
		
	}
	
}
