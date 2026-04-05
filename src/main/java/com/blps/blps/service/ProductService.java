package com.blps.blps.service;

import com.blps.blps.entity.Product;
import com.blps.blps.exception.ResourceNotFoundException;
import com.blps.blps.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product getProductById(Long id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Товар не найден: " + id));
    }
}
