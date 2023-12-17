package com.elk.mappers;

import com.elk.model.Employee;

public interface EmployeeMapper {

    public int saveEmployee(Employee employee);

    Employee getEmployeeById(Long id);
}
