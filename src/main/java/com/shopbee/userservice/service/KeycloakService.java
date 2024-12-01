package com.shopbee.userservice.service;

import com.shopbee.userservice.dto.*;
import com.shopbee.userservice.exception.ErrorResponse;
import com.shopbee.userservice.exception.UserServiceException;
import com.shopbee.userservice.mapper.UserMapper;
import com.shopbee.userservice.shared.Constant;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.common.util.CollectionUtil;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

@ApplicationScoped
public class KeycloakService {
    private final Keycloak keycloak;

    @Inject
    public KeycloakService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public List<GroupRepresentation> getRoleRepresentations() {
        return keycloak.realm(Constant.REALM).groups().groups();
    }

    public List<GroupRepresentation> getRoleRepresentations(String username) {
        UserResource userResource = getUserResourceByUsername(username);
        return userResource.groups();
    }

    public void assignRole(String username, String roleGroup) {
        GroupRepresentation roleGroupRepresentation = getGroupRepresentationByGroupName(roleGroup);
        UserResource userResource = getUserResourceByUsername(username);
        userResource.joinGroup(roleGroupRepresentation.getId());
    }

    public void removeRole(String username, String roleGroup) {
        GroupRepresentation roleGroupRepresentation = getGroupRepresentationByGroupName(roleGroup);
        UserResource userResource = getUserResourceByUsername(username);
        userResource.leaveGroup(roleGroupRepresentation.getId());
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
                throw new UserServiceException(keycloakResponse.getMessage(), response.getStatus());
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
                throw new UserServiceException("User does not associated with any Keycloak user", response.getStatus());
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

    public UserRepresentation getUserByUsername(String username) {
        List<UserRepresentation> users = getUsersResource().searchByUsername(username, true);
        if (CollectionUtil.isEmpty(users)) {
            throw new UserServiceException("User with username " + username + " not found", Response.Status.NOT_FOUND);
        }
        return users.getFirst();
    }

    private UsersResource getUsersResource() {
        return keycloak.realm(Constant.REALM).users();
    }

    private UserResource getUserResourceByUsername(String username) {
        String userId = getUserByUsername(username).getId();
        return getUsersResource().get(userId);
    }

    private GroupRepresentation getGroupRepresentationByGroupName(String roleGroup) {
        GroupsResource groupsResource = getGroupsResource();
        List<GroupRepresentation> groupRepresentations = groupsResource.groups();
        return groupRepresentations.stream()
                .filter(groupRepresentation -> groupRepresentation.getName().equalsIgnoreCase(roleGroup))
                .findFirst()
                .orElseThrow(() -> new UserServiceException("Role group " + roleGroup + " not found.", Response.Status.NOT_FOUND));
    }

    private GroupsResource getGroupsResource() {
        return keycloak.realm(Constant.REALM).groups();
    }
}
