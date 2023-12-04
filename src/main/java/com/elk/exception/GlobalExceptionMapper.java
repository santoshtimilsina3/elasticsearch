package com.elk.exception;

import com.elk.requestresponse.GenericResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.http.HttpStatus;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception exception) {
        return Response.status(HttpStatus.SC_BAD_REQUEST).entity(GenericResponse.builder()
                .statusCode(HttpStatus.SC_BAD_GATEWAY)
                .message(exception.getMessage())).build();
    }
}
