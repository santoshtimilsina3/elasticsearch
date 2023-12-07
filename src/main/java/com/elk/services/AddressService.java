package com.elk.services;

import com.elk.Utils.UniqueIdUtils;
import com.elk.exception.FailedToSaveException;
import com.elk.mappers.AddressMapper;
import com.elk.model.Address;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.apache.ibatis.session.SqlSession;

import java.util.logging.Logger;

@RequestScoped
public class AddressService {

    Logger logger = Logger.getLogger(this.getClass().getName());

    @Inject
    private SqlSession sqlSession;

    public Long createAddress(Address address) throws FailedToSaveException {
        long uniqueId = UniqueIdUtils.uniqueCurrentTimeNS();
        address.setId(uniqueId);
        try {
            AddressMapper addressMapper = sqlSession.getMapper(AddressMapper.class);
            addressMapper.saveAddress(address);
            sqlSession.commit();
        } catch (Exception e) {
            throw new FailedToSaveException("Unable to save Address with id " + uniqueId);
        }
        return uniqueId;
    }

    public Address checkAddress(Address address) throws Exception {
        Address newAddress = null;
        try {
            AddressMapper addressMapper = sqlSession.getMapper(AddressMapper.class);
            newAddress = (address.getId() != null) ? addressMapper.getAddressById(address.getId()) : new Address();
        } catch (Exception e) {
            throw new NotFoundException("Unable to get address with id " + address.getId());
        }
        return newAddress;
    }
}
