package com.shopbee.userservice.service;

import com.shopbee.userservice.dto.*;
import com.shopbee.userservice.exception.ErrorResponse;
import com.shopbee.userservice.exception.UserException;
import com.shopbee.userservice.mapper.UserMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.common.util.CollectionUtil;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

@ApplicationScoped
public class KeycloakService {

    private static final String REALM = "shopbee";

    private final Keycloak keycloak;

    @Inject
    public KeycloakService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public void createUser(UserCreation userCreation) {
        UserRepresentation user = UserMapper.toUserRepresentation(userCreation);
        createUser(user);
    }

    public void createUser(CustomerRegistration customerRegistration) {
        UserRepresentation user = UserMapper.toUserRepresentation(customerRegistration);
        user.setEnabled(true);
        createUser(user);
    }

    public void createUser(UserRepresentation userRepresentation) {
        try (Response response = getUsersResource().create(userRepresentation)) {
            if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
                ErrorResponse keycloakResponse = response.readEntity(ErrorResponse.class);
                throw new UserException(keycloakResponse.getErrorMessage(), response.getStatus());
            }
        }
    }

    public void updateUser(String username, UserUpdate userUpdate) {
        UserRepresentation user = UserMapper.bind(getUserByUsername(username), userUpdate);
        UserResource userResource = getUserResourceByUsername(username);
        userResource.update(user);
    }

    public void updateCustomer(String username, CustomerUpdate customerUpdate) {
        UserRepresentation user = getUserByUsername(username);
        user.setFirstName(customerUpdate.getFirstName());
        user.setLastName(customerUpdate.getLastName());
        user.setEmail(customerUpdate.getEmail());
        UserResource userResource = getUserResourceByUsername(username);
        userResource.update(user);
    }

    public void delete(String username) {
        String userId = getUserByUsername(username).getId();
        try (Response response = getUsersResource().delete(userId)) {
            if (response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) {
                throw new UserException("User does not associated with any Keycloak user", response.getStatus());
            }
        }
    }

    public void updatePassword(String username, PasswordUpdate passwordUpdate) {
        PasswordReset passwordReset = new PasswordReset(passwordUpdate.getNewPassword(), false);
        resetPassword(username, passwordReset);
    }

    public void resetPassword(String username, PasswordReset passwordReset) {
        UserResource userResource = getUserResourceByUsername(username);

        CredentialRepresentation passwordCredential = new CredentialRepresentation();
        passwordCredential.setType(CredentialRepresentation.PASSWORD);
        passwordCredential.setValue(passwordReset.getPassword());
        passwordCredential.setTemporary(passwordReset.isTemporary());

        userResource.resetPassword(passwordCredential);
    }

    public void forgotPassword(String username) {
        String userId = getUserByUsername(username).getId();
        getUsersResource().get(userId).executeActionsEmail(List.of("UPDATE_PASSWORD"));
    }

    private UserResource getUserResourceByUsername(String username) {
        String userId = getUserByUsername(username).getId();
        return getUsersResource().get(userId);
    }

    public UserRepresentation getUserByUsername(String username) {
        List<UserRepresentation> users = getUsersResource().searchByUsername(username, true);
        if (CollectionUtil.isEmpty(users)) {
            throw new UserException("User with username " + username + " not found", Response.Status.NOT_FOUND);
        }
        return users.getFirst();
    }

    private UsersResource getUsersResource() {
        return keycloak.realm(REALM).users();
    }

}
