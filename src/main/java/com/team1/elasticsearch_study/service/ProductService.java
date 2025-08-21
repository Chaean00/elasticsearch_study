package com.team1.elasticsearch_study.service;

import com.team1.elasticsearch_study.domain.product.Product;
import com.team1.elasticsearch_study.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // 상품 조회 메서드
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }
}
