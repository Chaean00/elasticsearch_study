package com.team1.elasticsearch_study.controller;

import com.team1.elasticsearch_study.domain.product.Product;
import com.team1.elasticsearch_study.domain.product.ProductDocument;
import com.team1.elasticsearch_study.service.ProductSearchService;
import com.team1.elasticsearch_study.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductSearchService productSearchService;

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable  Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping("/indexing")
    public ResponseEntity<String> startIndexing() {
        productSearchService.indexProductsFromDatabase();
        return ResponseEntity.ok("Bulk 인덱싱 실행 [비동기] - " + LocalDateTime.now());
    }
}
