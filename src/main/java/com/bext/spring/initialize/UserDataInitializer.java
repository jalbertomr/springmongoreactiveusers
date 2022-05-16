package com.bext.spring.initialize;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.bext.spring.repo.IUserRepository;
import com.bext.spring.user.domain.User;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Profile("production")
@Component
public class UserDataInitializer implements SmartInitializingSingleton{

	private IUserRepository userRepository;
	
	public UserDataInitializer(IUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public void afterSingletonsInstantiated() {
		log.info("profile: production initializing dataDB");
		userRepository.deleteAll().subscribe();
		
		User user = new User(1,"Daisy Ridley Production", null/*"daisy.ridley@email.com"*/, "daisypassword", Collections.singletonList("ADMIN"), Instant.now(), true);
		Mono<User> userSaved = userRepository.save( user);
		log.info("Save user {}", userSaved);
		User user2= new User(2,"Tom Raider Production","tom.raider@email.com","tom", Collections.singletonList("ADMIN"), Instant.now(), false);
		Mono<User> userSaved2 = userRepository.save(user);
		log.info("Save user {}", userSaved2);
		User user3= new User(3,"Peter Parker Production","peter.parker@email.com", "peterpassword", Stream.of("USER","ADMIN").collect(Collectors.toList()), Instant.now(), true);
		Mono<User> userSaved3 = userRepository.save(user);
		log.info("Save user {}", userSaved3);
		userRepository.saveAll(Arrays.asList(user, user2, user3)).subscribe();	
	}

}
