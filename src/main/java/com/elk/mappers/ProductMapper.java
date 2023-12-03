package com.elk.mappers;

import com.elk.model.Product;

public interface ProductMapper {
    Long saveProduct(Product product);
    public void deleteProduct(Long id);
    public void updateProduct(Product product);
}
