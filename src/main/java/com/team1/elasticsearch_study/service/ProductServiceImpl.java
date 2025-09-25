package com.team1.elasticsearch_study.service;

import com.team1.elasticsearch_study.domain.product.Product;
import com.team1.elasticsearch_study.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Transactional
    @Override
    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    @Transactional
    @Override
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void updateProduct(Long id, String name, BigDecimal price, String category) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다 [id]: " + id));

        product.update(name, price, category);
    }
}
