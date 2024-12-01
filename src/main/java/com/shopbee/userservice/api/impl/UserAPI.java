package com.shopbee.userservice.api.impl;

import com.shopbee.userservice.api.IUserAPI;
import com.shopbee.userservice.dto.*;
import com.shopbee.userservice.entity.User;
import com.shopbee.userservice.service.UserService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;

public class UserAPI implements IUserAPI {

    private final UserService userService;

    @Inject
    public UserAPI(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Response getAll(@BeanParam UserFilter filterCriteria,
                           @BeanParam UserSort sortingCriteria,
                           @BeanParam PageRequest pageRequest) {
        return Response.ok(userService.getByCriteria(filterCriteria, sortingCriteria, pageRequest)).build();
    }

    @Override
    public Response create(@Valid UserCreation userCreation, @Context UriInfo uriInfo) {
        User savedUser = userService.createNew(userCreation);
        URI uri = uriInfo.getAbsolutePathBuilder().path(savedUser.getId().toString()).build();
        return Response.created(uri).entity(savedUser).build();
    }

    @Override
    public Response delete(List<Long> ids) {
        userService.delete(ids);
        return Response.noContent().build();
    }

    @Override
    public Response getById(@PathParam("id") Long id) {
        return Response.ok(userService.getDetailsById(id)).build();
    }

    @Override
    public Response update(@PathParam("id") Long id, @Valid UserUpdate userUpdate) {
        userService.update(id, userUpdate);
        return Response.noContent().build();
    }

    @Override
    public Response resetPassword(@PathParam("id") Long id, @Valid PasswordReset passwordReset) {
        userService.resetPassword(id, passwordReset);
        return Response.noContent().build();
    }

    @Override
    public Response assignRoles(Long id, AssignRoleRequest assignRoleRequest) {
        return Response.ok().build();
    }


}
