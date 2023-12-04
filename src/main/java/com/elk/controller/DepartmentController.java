package com.elk.controller;


import com.elk.exception.NotSavedException;
import com.elk.model.Department;
import com.elk.requestresponse.GenericResponse;
import com.elk.services.DepartmentService;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.http.HttpStatus;

@Path("/department")
public class DepartmentController {

    @Inject
    private DepartmentService departmentService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveDepartment(Department department) throws NotSavedException {
        return Response.ok(GenericResponse.builder()
                .statusCode(HttpStatus.SC_CREATED)
                .message("Department Saved Successfully with id "+ departmentService.saveDepartment(department) )).build();
    }
}
