package com.team1.elasticsearch_study.repository;

import com.team1.elasticsearch_study.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findById(Long id);
}

