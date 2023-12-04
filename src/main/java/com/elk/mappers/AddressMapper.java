package com.elk.mappers;

import com.elk.model.Address;

public interface AddressMapper {
    public int saveAddress(Address address);

    public Address getAddressById(Long id);
}
