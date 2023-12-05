package com.elk.controller;

import com.elk.requestresponse.GenericResponse;
import com.elk.services.GlobalQueriesService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.http.HttpStatus;

import java.io.IOException;

@Path("/global")
public class GlobalController {

    @Inject
    private GlobalQueriesService globalQueriesService;

    @GET
    @Path("/searchAll/{text}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchAll(@PathParam("text") String text) throws IOException {
        return Response.ok(GenericResponse.builder()
                .statusCode(HttpStatus.SC_OK)
                .message("Search successful ")
                .data(globalQueriesService.searchInApplication(text))).build();
//        return Response.ok(globalQueriesService.searchInApplication(text), MediaType.APPLICATION_JSON).build();
    }
}
