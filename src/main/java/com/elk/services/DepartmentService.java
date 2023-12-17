package com.elk.services;


import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import com.elk.Utils.UniqueIdUtils;
import com.elk.exception.DatabaseOperationFailed;
import com.elk.exception.FailedToSaveException;
import com.elk.exception.NotSavedException;
import com.elk.mappers.DepartmentMapper;
import com.elk.model.Department;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.NotFoundException;
import lombok.SneakyThrows;
import org.apache.ibatis.session.SqlSession;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequestScoped
public class DepartmentService {

    Logger logger = Logger.getLogger(this.getClass().getName());
    @Inject
    private SqlSession sqlSession;


    @Inject
    private ElasticsearchAsyncClient elasticsearchAsyncClient;
    private static final String DEPARTMENT_INDEX = "index_department";


    public Long saveDepartment(Department department) throws NotSavedException {
        long uniqueId = UniqueIdUtils.uniqueCurrentTimeNS();
        department.setId(uniqueId);
        try {
            DepartmentMapper departmentMapper = sqlSession.getMapper(DepartmentMapper.class);
            if (departmentMapper.saveDepartment(department) != 1)
                throw new NotSavedException("Unable to save department");
            sqlSession.commit();
        } catch (Exception exception) {
            logger.log(Level.INFO, "Unable to save department");
            throw new NotSavedException(exception.getMessage());
        }
        return uniqueId;
    }

    public Department getDepartmentById(@NotNull Long id) {
        Department department = new Department();
        try {
            DepartmentMapper departmentMapper = sqlSession.getMapper(DepartmentMapper.class);
            department = departmentMapper.getDepartmentById(id);
        } catch (Exception e) {
            logger.log(Level.INFO, "Unable to find department with id " + id);
            throw new NotFoundException("Unable to find the department with id " + id);
        }
        return department;
    }

    public Long deleteDepartment(Long id) throws FailedToSaveException {
        DepartmentMapper departmentMapper = sqlSession.getMapper(DepartmentMapper.class);
        int checkLinkedEmployee = departmentMapper.checkLinkedEmployee(id);
        if (checkLinkedEmployee > 0)
            throw new FailedToSaveException("There are already Employees linked with the department " + id);
        int noOfRowDeleted = departmentMapper.deleteDepartment(id);
        if (noOfRowDeleted == 0) throw new DatabaseOperationFailed("Cannot find the department with id " + id);
        if (!deleteDeptInES(id)) {
            throw new FailedToSaveException("Unable to save in both databases");
        }
        sqlSession.commit();
        return id;
    }

    public Boolean deleteDeptInES(Long id) {
        try {
            elasticsearchAsyncClient.delete(DeleteRequest.of(builder -> builder
                    .id(String.valueOf(id))
                    .index(DEPARTMENT_INDEX)
            ));
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public Long updateDepartment(Department department) throws NotSavedException {
        Department departmentById = getDepartmentById(department.getId());
        if (departmentById.getId() == null)
            throw new NotFoundException("Unable to find department with id " + department.getId());
        try {
            DepartmentMapper departmentMapper = sqlSession.getMapper(DepartmentMapper.class);
            int noOfUpdatedDepartment = departmentMapper.updateDepartment(department);
            sqlSession.commit();

        } catch (Exception exception) {
            throw new NotSavedException("Unable to update Department");
        }
        return departmentById.getId();
    }


}
