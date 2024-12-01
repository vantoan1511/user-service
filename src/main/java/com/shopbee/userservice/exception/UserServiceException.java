package com.shopbee.userservice.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class UserServiceException extends WebApplicationException {

    public UserServiceException(String message, Response.Status status) {
        super(message, status);
    }

    public UserServiceException(String message, int status) {
        super(message, status);
    }
}
