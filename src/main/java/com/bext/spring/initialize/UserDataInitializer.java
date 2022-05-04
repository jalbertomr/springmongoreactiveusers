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

@Profile("production")
@Component
public class UserDataInitializer implements SmartInitializingSingleton{

	private IUserRepository userRepository;
	
	public UserDataInitializer(IUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public void afterSingletonsInstantiated() {
		User user = new User(1, "Daisy Ridley Production", "daisy.ridley@email.com", "daisypassword", Collections.singletonList("ADMIN"), Instant.now(), true);
		User user2= new User(2,"Tom Raider Production","tom.raider@email.com","tompassword", Collections.singletonList("ADMIN"), Instant.now(), false);
		User user3= new User(3,"Peter Parker Production","peter.parker@email.com", "peterpassword", Stream.of("USER","ADMIN").collect(Collectors.toList()), Instant.now(), true);
		
		userRepository.saveAll(Arrays.asList(user, user2, user3)).subscribe();	
	}

}
