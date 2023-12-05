package com.elk.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.elk.Utils.UniqueIdUtils;
import com.elk.exception.FailedToSaveExeption;
import com.elk.mappers.EmployeeMapper;
import com.elk.model.Department;
import com.elk.model.Employee;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequestScoped
public class EmployeeService {
    Logger logger = Logger.getLogger(this.getClass().getName());

    @Inject
    private DepartmentService departmentService;
    @Inject
    private SqlSession sqlSession;
    @Inject
    private ElasticsearchClient elasticsearchClient;
    private static final String DEPARTMENT_INDEX = "index_department";

    public Long saveEmployee(Employee employee) throws Exception {
        long uniqueId = UniqueIdUtils.uniqueCurrentTimeNS();
        employee.setId(uniqueId);

        Department isExists = departmentService.getDepartmentById(employee.getDepartmentId());
        CompletableFuture<Boolean> saveInPrimaryDb = saveInPrimaryDb(employee)
                .exceptionally(throwable -> {
                    logger.log(Level.INFO, "Error while saving the Employee in mysql db");
                    return false;
                });
        formDepartmentForESMapping(isExists, employee);
        CompletableFuture<Boolean> saveInSecondaryDb = saveInSecondaryDb(isExists);
        CompletableFuture.allOf(saveInPrimaryDb, saveInSecondaryDb).join();
        if (saveInPrimaryDb.get() && saveInSecondaryDb.get()) {
            sqlSession.commit();
            return uniqueId;
        }

        throw new FailedToSaveExeption("Unable to save Employee Data ");
    }

    private void formDepartmentForESMapping(Department isExists, Employee employee) {
        List<Employee> employees = List.of(employee);
        isExists.setEmployees((employees));
    }

    private void rollBackOperation(SqlSession session, Long uniqueId) throws IOException {
        session.rollback();
        elasticsearchClient.delete(builder -> builder.index(DEPARTMENT_INDEX)
                .id(String.valueOf(uniqueId)));
    }

    public CompletableFuture<Boolean> saveInPrimaryDb(Employee employee) {
        return CompletableFuture
                .supplyAsync(() -> {
                            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
                            employeeMapper.saveEmployee(employee);
                            return true;
                        }
                ).exceptionally(throwable -> {
                    throw new RuntimeException();
                });
    }

    public CompletableFuture<Boolean> saveInSecondaryDb(Department department) {
        System.out.println(department);
        return CompletableFuture
                .supplyAsync(() -> {
                    try {
                        elasticsearchClient.index(data -> data
                                .index(DEPARTMENT_INDEX)
                                .id(String.valueOf(department.getId()))
                                .document(department)
                        );
                    } catch (IOException e) {
                        logger.log(Level.INFO, "Error while saving data in elastic search");
                        return false;
                    }
                    return true;
                }).exceptionally(throwable -> {
                    logger.log(Level.INFO, "Error while saving data in elastic search");
                    return false;
                });
    }
}
