package com.team1.elasticsearch_study.service;

import com.team1.elasticsearch_study.domain.product.Product;

import java.math.BigDecimal;

public interface ProductService {
    void saveProduct(Product product);
    void deleteProductById(Long id);
    void updateProduct(Long id, String name, BigDecimal price, String category);
}
