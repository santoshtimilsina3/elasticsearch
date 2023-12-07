package com.elk.exception;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import com.elk.requestresponse.GenericResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.http.HttpStatus;

@Provider
public class ElasticsearchExceptionMapper implements ExceptionMapper<ElasticsearchException> {
    @Override
    public Response toResponse(ElasticsearchException exception) {
        return Response.status(Response.Status.BAD_REQUEST).entity(GenericResponse.builder()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .message(exception.getMessage()))
                .type(MediaType.APPLICATION_JSON).build();
    }
}
