package com.shopbee.userservice.exception;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException exception) {
        return Response.status(exception.getResponse().getStatus())
                .entity(ErrorResponse.builder().message("The resource you are looking for is not found.").build())
                .build();
    }

}
