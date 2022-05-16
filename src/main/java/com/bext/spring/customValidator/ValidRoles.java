package com.bext.spring.customValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;


@Target({ ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ValidRolesValidator.class})
@Documented
public @interface ValidRoles {
	String message() default "invalid role detected";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
