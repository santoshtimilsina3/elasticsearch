package com.elk.services;


import com.elk.Utils.UniqueIdUtils;
import com.elk.exception.NotSavedException;
import com.elk.mappers.DepartmentMapper;
import com.elk.model.Department;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.apache.ibatis.session.SqlSession;

@RequestScoped
public class DepartmentService {

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
}
