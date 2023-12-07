package com.elk.services;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.elk.Utils.UniqueIdUtils;
import com.elk.exception.DatabaseOperationFailed;
import com.elk.exception.FailedToSaveException;
import com.elk.mappers.EmployeeMapper;
import com.elk.model.Department;
import com.elk.model.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequestScoped
public class EmployeeService {
    Logger logger = Logger.getLogger(this.getClass().getName());

    @Inject
    private DepartmentService departmentService;
    @Inject
    private SqlSession sqlSession;
    @Inject
    private ElasticsearchClient elasticsearchClient;

    @Inject
    private ElasticsearchAsyncClient elasticsearchAsyncClient;
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

        CompletableFuture<Boolean> saveInSecondaryDb = saveInSecondaryDb(isExists, employee);
        CompletableFuture.allOf(saveInPrimaryDb, saveInSecondaryDb).join();
        if (saveInPrimaryDb.get() && saveInSecondaryDb.get()) {
            sqlSession.commit();
            return uniqueId;
        }
        rollBackUpdate(sqlSession, employee);
        throw new FailedToSaveException("Unable to save Employee Data ");
    }

    private void rollBackUpdate(SqlSession sqlSession, Employee employee) throws IOException {
        sqlSession.rollback();
        Department department = departmentExists(employee.getDepartmentId());
        List<Employee> updateEmployee = department.getEmployees().stream().filter(employee1 -> !(employee1.getId().equals(employee.getId()))).collect(Collectors.toList());
        department.setEmployees(updateEmployee);
        elasticsearchAsyncClient.update(UpdateRequest.of(builder ->
                        builder.index(DEPARTMENT_INDEX)
                                .upsert(department))
                , Department.class);
    }

    private void formDepartmentForESMapping(Department isExists, Employee employee) {
        if (isExists.getEmployees() != null) {
            isExists.getEmployees().add(employee);
        } else {
            isExists.setEmployees(List.of(employee));
        }
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
                    throw new DatabaseOperationFailed("failed  to save data");
                });
    }

    public CompletableFuture<Boolean> saveInSecondaryDb(Department department, Employee employee) {
        return CompletableFuture
                .supplyAsync(() -> {
                    try {
                        formDepartmentForESMapping(department, employee);
                        ObjectMapper objectMapper = new ObjectMapper();
                        StringReader stringReader = new StringReader(objectMapper.writeValueAsString(department));
                        IndexRequest<JsonData> indexRequest = IndexRequest.of(builder -> builder
                                .index(DEPARTMENT_INDEX)
                                .id(String.valueOf(department.getId()))
                                .withJson(stringReader));
                        elasticsearchClient.index(indexRequest);
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

    public Department departmentExists(Long deptId) throws IOException {
        SearchResponse<Department> departmentSearchResponse = elasticsearchClient.search(SearchRequest.of(builder ->
                builder.index(DEPARTMENT_INDEX)
                        .query(q ->
                                q.match(m ->
                                        m.field("_id")
                                                .query(deptId)
                                )
                        )
        ), Department.class);
        return departmentSearchResponse.hits().hits().stream().map(Hit::source).collect(Collectors.toList()).stream().findFirst().orElse(new Department());
    }

}
