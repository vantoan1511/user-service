package com.shopbee.userservice.validator;

import com.shopbee.userservice.validator.constraint.UniqueUsername;
import com.shopbee.userservice.dto.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@ApplicationScoped
public class UsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    UserRepository userRepository;

    public UsernameValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return userRepository.findByUsername(value).isEmpty();
    }
}
