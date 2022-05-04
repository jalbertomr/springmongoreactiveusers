package com.bext.spring.repo;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.bext.spring.user.domain.User;

import reactor.core.publisher.Flux;

@Repository
public interface IUserRepository extends ReactiveCrudRepository<User, Long>, ReactiveQueryByExampleExecutor<User>{
	
public Flux<User> findByEmailContainingIgnoreCase(String email);
/* Substituited by userService.findUserByExample that use findOne(Example<S>)
public Flux<User> findByEmailContainingAndRolesContainingAllIgnoreCaseAndEnabledIsTrue(String email, String role);
*/
}
