package com.shopbee.userservice.service;

import com.shopbee.userservice.dto.*;
import com.shopbee.userservice.entity.User;
import com.shopbee.userservice.exception.UserServiceException;
import com.shopbee.userservice.mapper.UserMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
@Transactional
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final KeycloakService keycloakService;

    @Inject
    public UserService(UserRepository userRepository,
                       KeycloakService keycloakService) {
        this.userRepository = userRepository;
        this.keycloakService = keycloakService;
    }

    public List<String> getRoles() {
        return keycloakService.getRoleRepresentations().stream().map(GroupRepresentation::getName).toList();
    }

    public List<String> getRoles(Long userId) {
        User user = getById(userId);
        return keycloakService.getRoleRepresentations(user.getUsername()).stream().map(GroupRepresentation::getName).toList();
    }

    public void assignRole(Long userId, String roleName) {
        User user = getById(userId);
        keycloakService.assignRole(user.getUsername(), roleName);
    }

    public void removeRole(Long userId, String roleName) {
        User user = getById(userId);
        keycloakService.removeRole(user.getUsername(), roleName);
    }

    public PagedResponse<UserDetails> getByCriteria(UserFilter userFilter, UserSort userSort, PageRequest pageRequest) {
        LOG.info("Start retrieving users... ");
        List<UserDetails> users = UserMapper.toUserDetailsList(userRepository.listAll())
                .stream()
                .map(this::withUserStatus)
                .toList();
        List<UserDetails> filteredUsers = applyFilter(users, userFilter);
        List<UserDetails> sortedUsers = sort(filteredUsers, userSort);
        LOG.info("Users: {}", sortedUsers);
        return PagedResponse.from(sortedUsers, pageRequest);
    }

    public UserDetails getDetailsById(Long id) {
        User user = getById(id);
        UserRepresentation keycloakUser = keycloakService.getUserByUsername(user.getUsername());
        List<String> roles = getRoles(id);
        UserDetails details = UserMapper.withAccountStatus(user, keycloakUser);
        details.setRoles(roles);
        return details;
    }

    public User getById(Long id) {
        return userRepository.findByIdOptional(id)
                .orElseThrow(() ->
                        new UserServiceException("User with ID " + id + " not found", Response.Status.NOT_FOUND));
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserServiceException("User with USERNAME " + username + " not found.", Response.Status.NOT_FOUND));
    }

    public User createNew(UserCreation userCreation) {
        if (userCreation == null) {
            throw new UserServiceException("Please provide all required information to create an account", Response.Status.BAD_REQUEST);
        }

        keycloakService.createUser(userCreation);

        User newUser = UserMapper.toUser(userCreation);
        userRepository.persist(newUser);
        return newUser;
    }

    public void update(Long id, UserUpdate userUpdate) {
        validateUniqueEmailUpdate(id, userUpdate.getEmail());

        User user = getById(id);
        user.setFirstName(userUpdate.getFirstName());
        user.setLastName(userUpdate.getLastName());
        user.setEmail(userUpdate.getEmail());
        user.setDisabledReason(userUpdate.getDisabledReason());

        keycloakService.updateUser(user.getUsername(), userUpdate);
    }

    public void resetPassword(Long id, PasswordReset passwordReset) {
        String username = getById(id).getUsername();
        keycloakService.resetPassword(username, passwordReset);
    }

    public void delete(List<Long> ids) {
        ids.forEach(this::delete);
    }

    private List<UserDetails> applyFilter(List<UserDetails> users, UserFilter userFilter) {
        if (ObjectUtils.allNull(userFilter.getKeyword(), userFilter.getEnabled(), userFilter.getEmailVerified())) {
            return users;
        }

        return users.stream()
                .filter(user -> matchKeyword(user, userFilter.getKeyword()))
                .filter(user -> matchEnabled(user, userFilter.getEnabled()))
                .filter(user -> matchEmailVerified(user, userFilter.getEmailVerified()))
                .toList();
    }

    private boolean matchKeyword(UserDetails user, String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return true;
        }

        StringBuilder lowerTextInfo = new StringBuilder(user.getUsername());
        lowerTextInfo.append(user.getFirstName());
        lowerTextInfo.append(user.getLastName());
        lowerTextInfo.append(user.getEmail());
        return lowerTextInfo.toString().toLowerCase().contains(keyword.toLowerCase());
    }

    private boolean matchEnabled(UserDetails user, Boolean enabled) {
        if (enabled == null) {
            return true;
        }

        return user.isEnabled() == enabled;
    }

    private boolean matchEmailVerified(UserDetails user, Boolean emailVerified) {
        if (emailVerified == null) {
            return true;
        }

        return user.isEmailVerified() == emailVerified;
    }

    private UserDetails withUserStatus(UserDetails userDetails) {
        UserRepresentation userKeycloak = keycloakService.getUserByUsername(userDetails.getUsername());
        userDetails.setEnabled(userKeycloak.isEnabled());
        userDetails.setEmailVerified(userKeycloak.isEmailVerified());
        return userDetails;
    }

    private List<UserDetails> sort(List<UserDetails> users, UserSort userSort) {
        List<UserDetails> sortedUsers = users.stream().sorted(userSort.getSortField().getComparator()).toList();
        if (!userSort.isAscending()) {
            return sortedUsers.reversed();
        }
        return sortedUsers;
    }

    private void validateUniqueEmailUpdate(Long id, String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            if (!user.getId().equals(id)) {
                throw new UserServiceException("Email " + email + " has associated with another account", Response.Status.CONFLICT);
            }
        });
    }

    private void delete(Long id) {
        String username = getById(id).getUsername();
        keycloakService.delete(username);
        userRepository.delete("id", id);
    }

}
