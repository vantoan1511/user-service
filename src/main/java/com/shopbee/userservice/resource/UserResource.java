package com.shopbee.userservice.resource;

import com.shopbee.userservice.entity.User;
import com.shopbee.userservice.dto.PageRequest;
import com.shopbee.userservice.dto.*;
import com.shopbee.userservice.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;

@Path("users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({Role.Constants.ADMIN_VALUE})
public class UserResource {

    UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GET
    public Response getAll(@BeanParam UserFilter filterCriteria,
                           @BeanParam UserSort sortingCriteria,
                           @BeanParam PageRequest pageRequest) {
        return Response.ok(userService.getByCriteria(filterCriteria, sortingCriteria, pageRequest)).build();
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Long id) {
        return Response.ok(userService.getDetailsById(id)).build();
    }

    @POST
    public Response create(@Valid UserCreation userCreation, @Context UriInfo uriInfo) {
        User savedUser = userService.createNew(userCreation);
        URI uri = uriInfo.getAbsolutePathBuilder().path(savedUser.getId().toString()).build();
        return Response.created(uri).entity(savedUser).build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Long id, @Valid UserUpdate userUpdate) {
        userService.update(id, userUpdate);
        return Response.noContent().build();
    }

    @PUT
    @Path("{id}/reset-password")
    public Response resetPassword(@PathParam("id") Long id, @Valid PasswordReset passwordReset) {
        userService.resetPassword(id, passwordReset);
        return Response.noContent().build();
    }

    @DELETE
    public Response delete(List<Long> ids) {
        userService.delete(ids);
        return Response.noContent().build();
    }
}
