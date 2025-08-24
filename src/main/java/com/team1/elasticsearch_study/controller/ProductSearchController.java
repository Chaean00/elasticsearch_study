package com.team1.elasticsearch_study.controller;

import com.team1.elasticsearch_study.domain.product.ProductDocument;
import com.team1.elasticsearch_study.service.ProductSearchIndexService;
import com.team1.elasticsearch_study.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/products/search")
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductSearchIndexService productSearchIndexService;
    private final ProductSearchService productSearchService;

    @PostMapping("/indexing")
    public ResponseEntity<String> startIndexing() {
        productSearchIndexService.indexProductsFromDatabase();
        return ResponseEntity.ok("Bulk 인덱싱 실행 [비동기] - " + LocalDateTime.now());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDocument> findById(@PathVariable Long id) {
        return productSearchService.findById(id) != null ?
                ResponseEntity.ok(productSearchService.findById(id)) :
                ResponseEntity.notFound().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductDocument>> findAll(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(productSearchService.findAll(page));
    }

    // 통합 검색 API - 모든 검색 조건을 RequestParam으로 받음
    @GetMapping("")
    public ResponseEntity<List<ProductDocument>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page) {

        // 검색 조건에 따라 적절한 메서드 호출
        if (name != null && !name.trim().isEmpty()) {
            log.info("이름으로 검색 - name: {}", name);
            return ResponseEntity.ok(productSearchService.findByProductsByName(name));
        }

        if (category != null && !category.trim().isEmpty()) {
            log.info("카테고리로 검색 - category: {}, page: {}", category, page);
            return ResponseEntity.ok(productSearchService.findByProductsCategory(category, page));
        }

        if (minPrice != null && maxPrice != null) {
            log.info("가격 범위로 검색 - minPrice: {}, maxPrice: {}", minPrice, maxPrice);
            return ResponseEntity.ok(productSearchService.findByProductsByPrice(minPrice, maxPrice));
        }

        // 조건이 없으면 빈 결과 반환
        return ResponseEntity.ok(List.of());
    }
}
