package com.elk.mappers;

import com.elk.model.Customer;

public interface CustomerMapper {
    public int saveCustomer(Customer customer);

    Long deleteCustomer(Long id);
}
