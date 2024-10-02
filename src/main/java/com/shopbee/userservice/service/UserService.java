package com.shopbee.userservice.service;

import com.shopbee.userservice.entity.User;
import com.shopbee.userservice.exception.UserException;
import com.shopbee.userservice.mapper.UserMapper;
import com.shopbee.userservice.dto.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
@Transactional
public class UserService {

    UserRepository userRepository;

    KeycloakService keycloakService;

    public UserService(UserRepository userRepository, KeycloakService keycloakService) {
        this.userRepository = userRepository;
        this.keycloakService = keycloakService;
    }

    public PagedResponse<UserDetails> getByCriteria(UserFilter userFilter, UserSort userSort, PageRequest pageRequest) {
        List<UserDetails> users = UserMapper.toUserDetailsList(userRepository.listAll())
                .stream()
                .map(this::withUserStatus)
                .toList();
        List<UserDetails> filteredUsers = applyFilter(users, userFilter);
        List<UserDetails> sortedUsers = sort(filteredUsers, userSort);
        return PagedResponse.from(sortedUsers, pageRequest);
    }

    public UserDetails getDetailsById(Long id) {
        User user = getById(id);
        UserRepresentation keycloakUser = keycloakService.getUserByUsername(user.getUsername());
        return UserMapper.withAccountStatus(user, keycloakUser);
    }

    public User getById(Long id) {
        return userRepository.findByIdOptional(id)
                .orElseThrow(() ->
                        new UserException("User with ID " + id + " not found", Response.Status.NOT_FOUND));
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserException("User with USERNAME " + username + " not found.", Response.Status.NOT_FOUND));
    }

    public User createNew(UserCreation userCreation) {
        if (userCreation == null) {
            throw new UserException("Please provide all required information to create an account", Response.Status.BAD_REQUEST);
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

        keycloakService.updateUser(user.getUsername(), userUpdate);
    }

    public void resetPassword(Long id, PasswordReset passwordReset) {
        String username = getById(id).getUsername();
        keycloakService.resetPassword(username, passwordReset);
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
        if (userSort.isDescending()) {
            return sortedUsers.reversed();
        }
        return sortedUsers;
    }

    private void validateUniqueEmailUpdate(Long id, String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            if (!user.getId().equals(id)) {
                throw new UserException("Email " + email + " has associated with another account", Response.Status.CONFLICT);
            }
        });
    }

    public void delete(List<Long> ids) {
        ids.forEach(this::delete);
    }

    private void delete(Long id) {
        String username = getById(id).getUsername();
        keycloakService.delete(username);
        userRepository.delete("id", id);
    }

}
