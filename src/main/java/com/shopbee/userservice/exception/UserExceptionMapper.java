package com.shopbee.userservice.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UserExceptionMapper implements ExceptionMapper<UserException> {

    @Override
    public Response toResponse(UserException exception) {
        return Response.status(exception.getResponse().getStatus())
                .entity(new ErrorResponse(exception.getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
