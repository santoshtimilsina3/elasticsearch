package com.elk.controller;


import com.elk.exception.FailedToSaveException;
import com.elk.model.Address;
import com.elk.requestresponse.GenericResponse;
import com.elk.services.AddressService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.http.HttpStatus;

@Path("/address")
@RequestScoped
public class AddressController {
    @Inject
    private AddressService addressService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAddress(Address address) throws FailedToSaveException {
        return Response.ok(GenericResponse.builder()
                .statusCode(HttpStatus.SC_CREATED)
                .message("Address created successfully with id " + addressService.createAddress(address))
        ).build();
    }
}
