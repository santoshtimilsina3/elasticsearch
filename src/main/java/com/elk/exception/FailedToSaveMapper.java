package com.elk.exception;

import com.elk.requestresponse.GenericResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.http.HttpStatus;

@Provider
public class FailedToSaveMapper implements ExceptionMapper<FailedToSaveExeption> {
    @Override
    public Response toResponse(FailedToSaveExeption exception) {
        return Response.status(HttpStatus.SC_CREATED).entity(GenericResponse.builder()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .message(exception.getMessage()).build()
        ).build();
    }
}
