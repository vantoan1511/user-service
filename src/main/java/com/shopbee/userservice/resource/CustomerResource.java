package com.shopbee.userservice.resource;

import com.shopbee.userservice.dto.*;
import com.shopbee.userservice.service.AuthenticationService;
import com.shopbee.userservice.service.CustomerService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

@Path("customers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
public class CustomerResource {

    CustomerService customerService;

    AuthenticationService authenticationService;

    public CustomerResource(CustomerService customerService,
                            AuthenticationService authenticationService) {
        this.customerService = customerService;
        this.authenticationService = authenticationService;
    }

    @GET
    @Path("{username}")
    @Authenticated
    public Response getByUsername(@PathParam("username") String username) {
        return Response.ok(customerService.getByUsername(username)).build();
    }

    @POST
    @PermitAll
    public Response register(@Valid CustomerRegistration customerRegistration, @Context UriInfo uriInfo) {
        Customer customer = customerService.register(customerRegistration);
        URI uri = uriInfo.getAbsolutePathBuilder().path(customer.getUsername()).build();
        return Response.created(uri).entity(customerRegistration).build();
    }

    @PUT
    @Path("{username}")
    @Authenticated
    public Response updateProfile(@PathParam("username") String username, @Valid CustomerUpdate customerUpdate) {
        authenticationService.authenticate(username);
        customerService.updateProfile(username, customerUpdate);
        return Response.noContent().build();
    }

    @PUT
    @Path("{username}/change-password")
    @Authenticated
    public Response changePassword(@PathParam("username") String username, @Valid PasswordUpdate passwordUpdate) {
        authenticationService.authenticate(username);
        customerService.updatePassword(username, passwordUpdate);
        return Response.noContent().build();
    }

    @POST
    @Path("forgot")
    public Response forgotPassword(ForgotRequest forgotRequest) {
        customerService.forgotPassword(forgotRequest);
        return Response.noContent().build();
    }
}
