package com.bext.spring.service.impl;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.bext.spring.repo.IUserRepository;
import com.bext.spring.service.IUserService;
import com.bext.spring.user.domain.User;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {

	private IUserRepository userRepository;
	
	public UserServiceImpl(IUserRepository userRepository) {
		this.userRepository = userRepository;
		createUserModel();
	}

	@Profile("!production")
	private void createUserModel() {
		log.info("UserService profile !production initilizing dataDB");
		userRepository.deleteAll();
		User user = new User(1, "Daisy Ridley", "daisy.ridley@email.com", "daisypassword", Collections.singletonList("ADMIN"), Instant.now(), true);
        Mono<User> usersaved = save(user);
        log.info("usersaved: {}", usersaved);
		User user2= new User(2 ,"Tom Raider","tom.raider@email.com","tompassword", Collections.singletonList("ADMIN"), Instant.now(), false);
		User user3= new User(3 ,"Peter Parker","peter.parker@email.com", "peterpassword", Stream.of("USER","ADMIN").collect(Collectors.toList()), Instant.now(), true);
		
		userRepository.saveAll(Arrays.asList(user, user2, user3)).subscribe();
	}

	public Flux<User> findAllLimit(@RequestParam(name = "limit", required = false, defaultValue="-1") long limit ) {
		if (limit == -1) {
			return userRepository.findAll();
		};
		return userRepository.findAll().take(limit);
	};
	
	public Mono<User> findById(Long Id) {
		return userRepository.findById(Id);
	};
	
	public Mono<User> findUserbyExample(User user) {
		ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreCase()
		.withMatcher("email", GenericPropertyMatcher::contains)
		.withMatcher("role",  GenericPropertyMatcher::contains)
		.withMatcher("enabled", GenericPropertyMatcher::exact);
		Example<User> example = Example.of(user, matcher);
		return userRepository.findOne(example);
	}
	
	public Mono<User> findUserbyExampleExact(User user){
		Example<User> exampleUser = Example.of(user);
		return userRepository.findOne(exampleUser);
	}
	
	public Mono<Boolean> existById(Long Id) {
		return userRepository.existsById(Id);
	};
	
	public Mono<User> save(User user) {
	   return userRepository.save(user);
	};
	
	@Override
	public Mono<ResponseEntity<Void>> deleteWithHttpResponse(Long id){
		return userRepository.findById(id)
		.flatMap( p -> userRepository.deleteById(id)
				 .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
				)
		.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
//	@Override
//	public Mono<ResponseEntity<ProductDto>> deleteWithResponseAndProduct(String id){
//				return repository.findById(id).map(AppUtils::entityToDto)
//		        .map( productFound -> {repository.deleteById(id).subscribe(); return productFound;}
//				).map(product -> ResponseEntity.ok(product))
//		.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
//	}
	
	public Mono<User> deleteById(Long id) {
	   return userRepository.findById(id)
			   .map( productFound -> {userRepository.deleteById(id).subscribe(); return productFound;});
	}

	@Override
	public Flux<User> findByEmailContainingIgnoreCase(String email) {
		return userRepository.findByEmailContainingIgnoreCase(email);
	}

}
