package com.bext.spring.service;

import org.springframework.web.bind.annotation.RequestParam;

import com.bext.spring.user.domain.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IUserService {
	
	public Flux<User> findAllLimit(@RequestParam(name = "limit", required = false, defaultValue="-1") long limit );
	public Mono<User> findById(Long Id);
	public boolean	existById(Long Id);
	public Mono<User> save(Mono<User> user);
	public Mono<User> deleteById(Long Id);

}
