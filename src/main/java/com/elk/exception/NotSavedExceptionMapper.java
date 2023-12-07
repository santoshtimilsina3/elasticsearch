package com.elk.exception;

import com.elk.requestresponse.GenericResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.http.HttpStatus;

@Provider
public class NotSavedExceptionMapper implements ExceptionMapper<NotSavedException> {
    @Override
    public Response toResponse(NotSavedException exception) {
        return Response.status(HttpStatus.SC_BAD_REQUEST).entity(GenericResponse.builder()
                        .statusCode(HttpStatus.SC_BAD_REQUEST).message(exception.getMessage()))
                .type(MediaType.APPLICATION_JSON).build();
    }
}
