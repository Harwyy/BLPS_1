package com.blps.blps.mapper;

import com.blps.blps.dto.response.ProductResponse;
import com.blps.blps.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        if (product == null) return null;
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice());
    }
}
