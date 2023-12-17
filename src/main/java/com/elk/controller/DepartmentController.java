package com.elk.controller;


import com.elk.exception.FailedToSaveException;
import com.elk.exception.NotSavedException;
import com.elk.model.Department;
import com.elk.requestresponse.GenericResponse;
import com.elk.services.DepartmentService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.http.HttpStatus;

@Path("/department")
@Produces(MediaType.APPLICATION_JSON)
public class DepartmentController {

    @Inject
    private DepartmentService departmentService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveDepartment(Department department) throws NotSavedException {
        return Response.ok(GenericResponse.builder()
                .statusCode(HttpStatus.SC_CREATED)
                .message("Department Saved Successfully with id " + departmentService.saveDepartment(department))).build();
    }

    @POST
    @Path("/update")
    public Response updateDepartment(@Valid Department department) throws NotSavedException {
        return Response.ok(GenericResponse.builder()
                .statusCode(HttpStatus.SC_OK)
                .message("Update Successful !!" + departmentService.updateDepartment(department))
        ).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteDepartment(@PathParam("id") @NotNull Long id) throws FailedToSaveException {
        return Response.ok(GenericResponse.builder()
                .statusCode(HttpStatus.SC_OK)
                .message("Delete Successful of id " + departmentService.deleteDepartment(id))
        ).build();
    }

}
