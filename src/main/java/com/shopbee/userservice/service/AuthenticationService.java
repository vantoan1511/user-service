package com.shopbee.userservice.service;

import com.shopbee.userservice.exception.UserException;
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
            throw new UserException("Unauthorized", Response.Status.UNAUTHORIZED);
        }

        if (!getCurrentUsername().equals(username)) {
            throw new UserException("You do not have permissions", Response.Status.FORBIDDEN);
        }
    }

    public String getCurrentUsername() {
        return securityIdentity.getPrincipal().getName();
    }
}
