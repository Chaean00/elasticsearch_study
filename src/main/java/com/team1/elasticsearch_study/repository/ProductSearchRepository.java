package com.team1.elasticsearch_study.repository;

import com.team1.elasticsearch_study.domain.product.ProductDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {

    // 이름으로 검색 (부분 일치)
    // findByName - 완전 일치 검색
    List<ProductDocument> findByNameContaining(String name);

    // 가격 범위로 검색
    List<ProductDocument> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // 카테고리로 검색
    List<ProductDocument> findByCategory(String category, Pageable pageable);
}