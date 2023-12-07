package com.elk.mappers;

import com.elk.model.Department;

public interface DepartmentMapper {

    public int saveDepartment(Department department);

    Department getDepartmentById(Long id);

    int checkLinkedEmployee(Long id);

   int  deleteDepartment(Long id);
}
