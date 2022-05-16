package com.bext.spring.user.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Document(collection = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
	@Id
	private long id;
	
	@Builder.Default
	private String name = "";
	
	@NotEmpty @NotNull(message="email must be supplied")
	@Builder.Default
	private String email = "";
	
	@Size(min=8,max=254)
	@Builder.Default
	private String password = "";
	
	@Builder.Default
	private List<String> roles = new ArrayList<>();
	
	@Builder.Default
	private Instant lastLogin = Instant.now();
	
	private boolean enabled;
	
	public User(long id) {
	  this.id = id;
	}
	
//	public User(long id, String name, String email, String password, List<String> roles, Instant lastLogin,
//			boolean enabled) {
//		super();
//		this.id = id;
//		this.name = name;
//		this.email = email;
//		this.password = password;
//		this.roles = roles;
//		this.lastLogin = lastLogin;
//		this.enabled = enabled;
//	}
//	
//	public User(String name, String email, String password, List<String> roles, Instant lastLogin,
//			boolean enabled) {
//		this.name = name;
//		this.email = email;
//		this.password = password;
//		this.roles = roles;
//		this.lastLogin = lastLogin;
//		this.enabled = enabled;
//	}
//	
//	public long getId() {
//		return id;
//	}
//	public void setId(long id) {
//		this.id = id;
//	}
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
//	public String getEmail() {
//		return email;
//	}
//	public void setEmail(String email) {
//		this.email = email;
//	}
//	public String getPassword() {
//		return password;
//	}
//	public void setPassword(String password) {
//		this.password = password;
//	}
//	public List<String> getRoles() {
//		return roles;
//	}
//	public void setRoles(List<String> roles) {
//		this.roles = roles;
//	}
//	public Instant getLastLogin() {
//		return lastLogin;
//	}
//	public void setLastLogin(Instant lastLogin) {
//		this.lastLogin = lastLogin;
//	}
//	public boolean isEnabled() {
//		return enabled;
//	}
//	public void setEnabled(boolean enabled) {
//		this.enabled = enabled;
//	}

}
