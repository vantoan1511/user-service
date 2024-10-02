package com.shopbee.userservice.validator.constraint;

import com.shopbee.userservice.validator.EmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Constraint(validatedBy = EmailValidator.class)
public @interface UniqueEmail {
    String message() default "Email is associated with another account";

    Class<? extends Payload>[] payload() default {};

    Class<?>[] groups() default {};
}
