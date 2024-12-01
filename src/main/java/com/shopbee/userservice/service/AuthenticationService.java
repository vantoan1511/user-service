package com.shopbee.userservice.service;

import com.shopbee.userservice.exception.UserServiceException;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class AuthenticationService {
    private final SecurityIdentity securityIdentity;

    public AuthenticationService(SecurityIdentity securityIdentity) {
        this.securityIdentity = securityIdentity;
    }

    public void authenticate(String username) {
        if (username == null) {
            throw new UserServiceException("Unauthorized", Response.Status.UNAUTHORIZED);
        }

        if (!getCurrentUsername().equals(username)) {
            throw new UserServiceException("You do not have permissions", Response.Status.FORBIDDEN);
        }
    }

    public String getCurrentUsername() {
        return securityIdentity.getPrincipal().getName();
    }
}
