package com.elk.exception;

import com.elk.requestresponse.GenericResponse;
import jakarta.mail.event.MailEvent;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.http.HttpStatus;

@Provider
public class FailedToSaveExceptionMapper implements ExceptionMapper<FailedToSaveException> {
    @Override
    public Response toResponse(FailedToSaveException exception) {
        return Response.status(HttpStatus.SC_BAD_REQUEST).entity(GenericResponse.builder()
                        .statusCode(HttpStatus.SC_BAD_REQUEST)
                        .message(exception.getMessage())
                        .build()
                )
                .type(MediaType.APPLICATION_JSON).build();
    }
}
