package com.shopbee.userservice.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UserServiceExceptionMapper implements ExceptionMapper<UserServiceException> {

    @Override
    public Response toResponse(UserServiceException exception) {
        return Response.status(exception.getResponse().getStatus())
                .entity(ErrorResponse.builder().message(exception.getMessage()).build())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
