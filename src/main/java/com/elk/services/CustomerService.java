package com.elk.services;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.elk.Utils.Utils;
import com.elk.exception.FailedToSaveExeption;
import com.elk.mappers.CustomerMapper;
import com.elk.model.Address;
import com.elk.model.Customer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@RequestScoped
public class CustomerService {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Inject
    private SqlSession sqlSession;

    @Inject
    private AddressService addressService;
    @Inject
    private ElasticsearchClient elasticsearchClient;
    private static final String CUSTOMER_INDEX = "index_customer";

    public List<Customer> getCustomers() throws IOException {
        List<Customer> allCustomers = List.of();
        try {
            SearchResponse<Customer> search = elasticsearchClient.search(s -> s
                            .index(CUSTOMER_INDEX)
                            .query(q ->
                                    q.matchAll(m -> m
                                            .queryName("{}"))),
                    Customer.class
            );
            allCustomers = search.hits().hits().stream().map(Hit::source).collect(Collectors.toList());
        } catch (Exception exception) {
            logger.log(Level.INFO, "Unable to find customers");
        }
        return allCustomers;
    }

    public long saveCustomer(Customer customer) throws Exception {
        long uniqueId = Utils.uniqueCurrentTimeNS();
        customer.setId(uniqueId);

        Address isAddressExits = addressService.checkAddress(customer.getAddress());
        if (isAddressExits.getId() == null) throw new NotFoundException("Unable to find address you provided");
        customer.setAddress(isAddressExits);
        CompletableFuture<Boolean> isSaveInPrimaryDb = saveInPrimaryDb(customer)
                .exceptionally(throwable -> {
                    logger.log(Level.INFO, "Unable to save Customer Information in MYSql Database");
                    return false;
                });

        CompletableFuture<Boolean> isSaveInSecondaryDb = saveInSecondaryDb(customer);
        CompletableFuture.allOf(isSaveInPrimaryDb, isSaveInSecondaryDb).join();
        if (isSaveInPrimaryDb.get() && isSaveInSecondaryDb.get()) {
            sqlSession.commit();
            return uniqueId;
        }
        rollBackOperation(sqlSession, uniqueId);
        throw new FailedToSaveExeption("Unable to save Customer Data");
    }

    private CompletableFuture<Boolean> saveInSecondaryDb(Customer customer) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                 elasticsearchClient.index(data -> data
                        .index(CUSTOMER_INDEX)
                        .id(String.valueOf(customer.getId()))
                        .document(customer)
                );
                return true;
            } catch (Exception e) {
                logger.log(Level.INFO, "Unable to save data in Elasticsearch");
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> saveInPrimaryDb(Customer customer) throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            CustomerMapper customerMapper = sqlSession.getMapper(CustomerMapper.class);
            int noOfRowsAffected = customerMapper.saveCustomer(customer);
            return noOfRowsAffected == 1;
        });
    }

    private void rollBackOperation(SqlSession session, Long uniqueId) throws IOException {
        session.rollback();
        elasticsearchClient.delete(builder -> builder.index(CUSTOMER_INDEX)
                .id(String.valueOf(uniqueId)));
    }
}
