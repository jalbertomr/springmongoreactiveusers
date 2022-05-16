package com.bext.spring.user.domain;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;

import com.bext.spring.customValidator.ValidRoles;
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
	@ValidRoles
	private List<String> roles = new ArrayList<>();
	
	@Builder.Default
	private Instant lastLogin = Instant.now();
	
	private boolean enabled;
	
	public User(long id) {
	  this.id = id;
	}
}


