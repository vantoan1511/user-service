package com.shopbee.userservice.validator;

import com.shopbee.userservice.validator.constraint.UniqueEmail;
import com.shopbee.userservice.dto.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@ApplicationScoped
public class EmailValidator implements ConstraintValidator<UniqueEmail, String> {

    UserRepository userRepository;

    public EmailValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return userRepository.findByEmail(value).isEmpty();
    }
}
