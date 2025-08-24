package com.team1.elasticsearch_study.repository;

import com.team1.elasticsearch_study.domain.product.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // 커서 기반 페이징을 위한 메서드
    @Query("SELECT p FROM Product p WHERE p.id > :lastId ORDER BY p.id ASC")
    List<Product> findProductsAfterIdWithLimit(@Param("lastId") Long lastId, Pageable pageable);
    
    // 전체 데이터 수 조회
    @Query("SELECT COUNT(p) FROM Product p")
    long getTotalProductCount();
}

