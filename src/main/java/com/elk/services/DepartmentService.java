package com.elk.services;


import com.elk.Utils.UniqueIdUtils;
import com.elk.exception.NotSavedException;
import com.elk.mappers.DepartmentMapper;
import com.elk.model.Department;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.NotFoundException;
import org.apache.ibatis.session.SqlSession;

import java.util.logging.Level;
import java.util.logging.Logger;

@RequestScoped
public class DepartmentService {

    Logger logger = Logger.getLogger(this.getClass().getName());
    @Inject
    private SqlSession sqlSession;

    public Long saveDepartment(Department department) throws NotSavedException {
        long uniqueId = UniqueIdUtils.uniqueCurrentTimeNS();
        department.setId(uniqueId);
        try {
            DepartmentMapper departmentMapper = sqlSession.getMapper(DepartmentMapper.class);
            if (departmentMapper.saveDepartment(department) != 1)
                throw new NotSavedException("Unable to save department");
            sqlSession.commit();
        } catch (Exception exception) {
            throw new NotSavedException(exception.getMessage());
        }
        return uniqueId;
    }

    public Department getDepartmentById(@NotNull Long id) {
        Department department = null;
        try {
            DepartmentMapper departmentMapper = sqlSession.getMapper(DepartmentMapper.class);
            department = departmentMapper.getDepartmentById(id);
            if (department.getId() == null) throw new NotFoundException("Unable to find the department with id " + id);
        } catch (Exception e) {
            logger.log(Level.INFO, "Unable to find department with id " + id);
            throw new NotFoundException("Unable to find the department with id " + id);
        }
        return department;
    }
}
