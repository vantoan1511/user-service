package com.shopbee.userservice.validator.constraint;

import com.shopbee.userservice.validator.UsernameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = UsernameValidator.class)
public @interface UniqueUsername {

    String message() default "Username has already existed";

    Class<? extends Payload>[] payload() default {};

    Class<?>[] groups() default {};
}
