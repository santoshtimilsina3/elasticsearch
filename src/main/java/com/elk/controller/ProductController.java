package com.elk.controller;


import com.elk.model.Product;
import com.elk.requestresponse.GenericResponse;
import com.elk.services.ProductService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.http.HttpStatus;

import java.io.IOException;

@Singleton
@Path("/products")
public class ProductController {

    @Inject
    private ProductService productService;

    @GET
    public Response getHello() {
        return Response.ok().build();
    }

    @POST
    public Response saveProduct(Product product) throws Exception {
        long savedProduct = productService.saveProduct(product);
        return Response.ok().entity(savedProduct).build();
    }

    @POST
    @Path("{id}")
    public Response updateProduct(Product product,@PathParam("id")Long id) throws Exception {
        return Response.ok(productService.updateOperation(product)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProducts() throws IOException {
        return Response.ok(productService.getProducts()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{name}")
    public Response getProductByName(@PathParam("name") String name) throws IOException {
        return Response.ok(GenericResponse.builder().data(productService.getProductByName(name))
                .statusCode(HttpStatus.SC_OK)
                .message("Product get sucessfully")
        ).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteByid(@PathParam("id") Long id) throws Exception {
        return Response.ok(productService.deleteById(id)).build();
    }


}
