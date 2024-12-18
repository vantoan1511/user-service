package com.shopbee.userservice.api;

import com.shopbee.userservice.dto.*;
import com.shopbee.userservice.shared.Constant;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;

@Path("users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({Constant.ADMIN})
public interface IUserAPI {

    @GET
    Response getAll(@BeanParam UserFilter filterCriteria,
                    @BeanParam UserSort sortingCriteria,
                    @BeanParam PageRequest pageRequest);

    @POST
    Response create(@Valid UserCreation userCreation, @Context UriInfo uriInfo);

    @DELETE
    Response delete(List<Long> ids);

    @GET
    @Path("{id}")
    Response getById(@PathParam("id") Long id);

    @PUT
    @Path("{id}")
    Response update(@PathParam("id") Long id, @Valid UserUpdate userUpdate);

    @PUT
    @Path("{id}/reset-password")
    Response resetPassword(@PathParam("id") Long id, @Valid PasswordReset passwordReset);

    @GET
    @Path("{id}/roles")
    Response getRolesByUserId(@PathParam("id") Long id);

    @PATCH
    @Path("{id}/roles/{roleName}")
    Response assignRole(@PathParam("id") Long id, @PathParam("roleName") String roleName);

    @DELETE
    @Path("{id}/roles/{roleName}")
    Response removeRole(@PathParam("id") Long id, @PathParam("roleName") String roleName);

    @GET
    @Path("roles")
    Response getRoles();

}
