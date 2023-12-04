package com.elk.controller;

import com.elk.model.Customer;
import com.elk.requestresponse.GenericResponse;
import com.elk.services.CustomerService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.http.HttpStatus;

import java.io.IOException;

@Path("/customers")
@RequestScoped
public class CustomerController {

    @Inject
    private CustomerService customerService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomers() throws IOException {
        return Response.ok(GenericResponse.builder().statusCode(HttpStatus.SC_OK)
                .message("Get Customers Successfully")
                .data(customerService.getCustomers())).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveCustomer(Customer customer) throws Exception {
        long insertedId = customerService.saveCustomer(customer);
        return Response.ok(GenericResponse.builder()
                .statusCode(HttpStatus.SC_OK)
                .message("Customer Saved Successfully with id "+ insertedId)
                .data(insertedId)).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCustomer(@NotNull Long id){
        return Response.ok(GenericResponse.builder()
                .statusCode(HttpStatus.SC_OK)
                .message("Customer deleted with id "+ customerService.delete)
        ).build();
    }
}
