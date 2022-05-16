package com.bext.spring.customValidator;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class ValidRolesValidator implements ConstraintValidator<ValidRoles, Collection<String>> {
	
	private List<String> validRoles = Arrays.asList("ROLE_USER","ROLE_ADMIN");
	
	@Override
	public boolean isValid(Collection<String> collection, ConstraintValidatorContext context) {
		return collection.stream().allMatch(validRoles::contains);
	}
}
