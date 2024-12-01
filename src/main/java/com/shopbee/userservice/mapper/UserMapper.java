package com.shopbee.userservice.mapper;

import com.shopbee.userservice.dto.*;
import com.shopbee.userservice.entity.User;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Optional;

public class UserMapper {

    private UserMapper() {
    }

    public static List<UserDetails> toUserDetailsList(List<User> users) {
        return users.stream().map(UserMapper::toUserDetails).toList();
    }

    public static UserDetails withAccountStatus(User user, UserRepresentation keycloakUser) {
        UserDetails userDetails = UserMapper.toUserDetails(user);
        userDetails.setEnabled(keycloakUser.isEnabled());
        userDetails.setEmailVerified(keycloakUser.isEmailVerified());
        userDetails.setRoles(keycloakUser.getRealmRoles());
        return userDetails;
    }

    public static UserDetails toUserDetails(User source) {
        return Optional.ofNullable(source)
                .map(src -> UserDetails.builder()
                        .id(src.getId())
                        .firstName(src.getFirstName())
                        .lastName(src.getLastName())
                        .email(src.getEmail())
                        .username(src.getUsername())
                        .phone(src.getPhone())
                        .address(src.getAddress())
                        .address1(src.getAddress1())
                        .address2(src.getAddress2())
                        .address3(src.getAddress3())
                        .address4(src.getAddress4())
                        .gender(src.getGender())
                        .createdAt(src.getCreatedAt())
                        .modifiedAt(src.getModifiedAt())
                        .disableReason(src.getDisabledReason())
                        .build())
                .orElse(new UserDetails());
    }

    public static Customer toCustomer(User source) {
        return Optional.ofNullable(source)
                .map(src -> Customer.builder()
                        .id(src.getId())
                        .firstName(src.getFirstName())
                        .lastName(src.getLastName())
                        .email(src.getEmail())
                        .username(src.getUsername())
                        .phone(src.getPhone())
                        .address(src.getAddress())
                        .address1(src.getAddress1())
                        .address2(src.getAddress2())
                        .address3(src.getAddress3())
                        .address4(src.getAddress4())
                        .gender(src.getGender())
                        .createdAt(src.getCreatedAt())
                        .modifiedAt(src.getModifiedAt())
                        .build())
                .orElse(new Customer());
    }

    public static UserRepresentation toUserRepresentation(CustomerRegistration customerRegistration) {
        return Optional.ofNullable(customerRegistration)
                .map(UserMapper::toUserCreation)
                .map(UserMapper::toUserRepresentation)
                .orElse(new UserRepresentation());
    }

    public static UserRepresentation toUserRepresentation(UserCreation userCreation) {
        UserRepresentation user = new UserRepresentation();

        Optional.ofNullable(userCreation.getPassword()).ifPresent(password -> {
            CredentialRepresentation passwordCredential = new CredentialRepresentation();
            passwordCredential.setType(CredentialRepresentation.PASSWORD);
            passwordCredential.setValue(userCreation.getPassword());
            passwordCredential.setTemporary(userCreation.isTemporary());
            user.setCredentials(List.of(passwordCredential));
        });

        user.setUsername(userCreation.getUsername());
        user.setEmail(userCreation.getEmail());
        user.setEnabled(userCreation.isEnabled());
        user.setEmailVerified(userCreation.isEmailVerified());
        user.setFirstName(userCreation.getFirstName());
        user.setLastName(userCreation.getLastName());

        return user;
    }

    public static User toUser(CustomerRegistration source) {
        return Optional.ofNullable(source)
                .map(UserMapper::toUserCreation)
                .map(UserMapper::toUser)
                .orElse(new User());
    }

    public static UserCreation toUserCreation(CustomerRegistration source) {
        return Optional.ofNullable(source)
                .map(src -> UserCreation.builder()
                        .firstName(src.getFirstName())
                        .lastName(src.getLastName())
                        .email(src.getEmail())
                        .username(src.getUsername())
                        .password(src.getPassword())
                        .build())
                .orElse(new UserCreation());
    }

    public static User toUser(UserCreation userCreation) {
        return Optional.ofNullable(userCreation)
                .map(src -> User.builder()
                        .firstName(src.getFirstName())
                        .lastName(src.getLastName())
                        .email(src.getEmail())
                        .username(src.getUsername())
                        .gender(Gender.UNKNOWN)
                        .build())
                .orElse(new User());
    }

    public static UserRepresentation bind(UserRepresentation source, UserUpdate userUpdate) {
        source.setFirstName(userUpdate.getFirstName());
        source.setLastName(userUpdate.getLastName());
        source.setEmail(userUpdate.getEmail());
        source.setEnabled(userUpdate.isEnabled());
        source.setEmailVerified(userUpdate.isEmailVerified());
        return source;
    }
}
