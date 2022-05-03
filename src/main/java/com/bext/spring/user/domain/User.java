package com.bext.spring.user.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;

@Document
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
	@Id
	private long id;
	private String name = "";
	private String email = "";
	private String password = "";
	private List<String> roles = new ArrayList<>();
	private Instant lastLogin = Instant.now();
	private boolean enabled;
	
	public User(long id, String name, String email, String password, List<String> roles, Instant lastLogin,
			boolean enabled) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.roles = roles;
		this.lastLogin = lastLogin;
		this.enabled = enabled;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	public Instant getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(Instant lastLogin) {
		this.lastLogin = lastLogin;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	
}
