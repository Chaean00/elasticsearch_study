package com.team1.elasticsearch_study.service;

import com.team1.elasticsearch_study.domain.product.ProductDocument;
import com.team1.elasticsearch_study.dto.SearchProductsRes;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductSearchService {
    ProductDocument findById(Long id);
    List<ProductDocument> findAll(int pageNum);
    List<SearchProductsRes> searchProducts(String name, String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
}
