package com.shopbee.userservice.service;

import com.shopbee.userservice.dto.*;
import com.shopbee.userservice.entity.User;
import com.shopbee.userservice.exception.UserException;
import com.shopbee.userservice.mapper.UserMapper;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;

import java.util.Optional;

@ApplicationScoped
@Transactional
public class CustomerService {

    UserRepository userRepository;

    UserService userService;

    KeycloakService keycloakService;

    SecurityIdentity securityIdentity;

    public CustomerService(UserRepository userRepository,
                           UserService userService,
                           KeycloakService keycloakService,
                           SecurityIdentity securityIdentity) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.keycloakService = keycloakService;
        this.securityIdentity = securityIdentity;
    }

    public Customer getByUsername(String username) {
        return UserMapper.toCustomer(userService.getByUsername(username));
    }

    public Customer register(CustomerRegistration customerRegistration) {
        keycloakService.createUser(customerRegistration);
        User user = UserMapper.toUser(customerRegistration);
        userRepository.persist(user);
        return UserMapper.toCustomer(user);
    }

    public void updateProfile(String username, @Valid CustomerUpdate customerUpdate) {
        validateUniqueEmailUpdate(username, customerUpdate.getEmail());
        User user = userService.getByUsername(username);
        user.setEmail(customerUpdate.getEmail());
        user.setFirstName(customerUpdate.getFirstName());
        user.setLastName(customerUpdate.getLastName());
        user.setAddress(customerUpdate.getAddress());
        user.setAddress1(customerUpdate.getAddress1());
        user.setAddress2(customerUpdate.getAddress2());
        user.setAddress3(customerUpdate.getAddress3());
        user.setAddress4(customerUpdate.getAddress4());
        user.setGender(customerUpdate.getGender());
        user.setPhone(customerUpdate.getPhone());
        keycloakService.updateCustomer(username, customerUpdate);
    }

    public void updatePassword(String username, PasswordUpdate passwordUpdate) {
        if (passwordUpdate == null) {
            throw new UserException("Password update request is invalid", Response.Status.BAD_REQUEST);
        }
        keycloakService.updatePassword(username, passwordUpdate);
    }

    public void forgotPassword(ForgotRequest forgotRequest) {
        String email = Optional.ofNullable(forgotRequest).map(ForgotRequest::getEmail).orElse(null);
        userRepository.findByEmail(email)
                .ifPresent(user -> keycloakService.forgotPassword(user.getUsername()));
    }

    private void validateUniqueEmailUpdate(String username, String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            if (!user.getUsername().equals(username)) {
                throw new UserException("Email " + email + " has associated with another account", Response.Status.CONFLICT);
            }
        });
    }

}
