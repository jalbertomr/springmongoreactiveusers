package com.bext.spring.service.impl;

import java.time.Instant;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.bext.spring.service.IUserService;
import com.bext.spring.user.domain.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements IUserService {

	private Flux<User> users;

	public UserServiceImpl() {
		users = createUserModel();
	}

	private Flux<User> createUserModel() {
		User user = new User(1, "Daisy Ridley", "daisy.ridley@email.com", "daisypassword", Collections.singletonList("ADMIN"), Instant.now(), true);
		User user2= new User(2,"Tom Raider","tom.raider@email.com","tompassword", Collections.singletonList("ADMIN"), Instant.now(), false);
		User user3= new User(3,"Peter Parker","peter.parker@email.com", "peterpassword", Stream.of("USER","ADMIN").collect(Collectors.toList()), Instant.now(), true);
		return Flux.just( user, user2, user3);
	}

	public Flux<User> findAllLimit(@RequestParam(name = "limit", required = false, defaultValue="-1") long limit ) {
		if (limit == -1) {
			return users;
		};
		return users.take(limit);
	};
	
	public Mono<User> findById(Long Id) {
		return Mono.from(users.filter(user -> user.getId() == Id));
	};
	
	public boolean	existById(Long Id) {
		return users.filter(user -> user.getId() == Id).count().block() > 0;
	};
	
	public void	save(Mono<User> user) {
	   users = users.mergeWith(user);	
	};
	
	public void	deleteById(Long Id) {
	   users = users.filter(user -> user.getId() != Id);
	}

}
