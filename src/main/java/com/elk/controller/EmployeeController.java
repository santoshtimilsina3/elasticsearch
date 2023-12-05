package com.elk.controller;

import com.elk.model.Employee;
import com.elk.requestresponse.GenericResponse;
import com.elk.services.EmployeeService;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.http.HttpStatus;

@Path("/employee")
public class EmployeeController {

    @Inject
    private EmployeeService employeeService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveEmployee(Employee employee) throws Exception {
        return Response.ok(GenericResponse.builder()
                .statusCode(HttpStatus.SC_CREATED)
                .message("Employee Created with id " + employeeService.saveEmployee(employee))
        ).build();
    }
}
