package com.team1.elasticsearch_study.controller;

import com.team1.elasticsearch_study.domain.product.ProductDocument;
import com.team1.elasticsearch_study.dto.SearchProductsRes;
import com.team1.elasticsearch_study.service.ProductSearchIndexService;
import com.team1.elasticsearch_study.service.ProductSearchServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final ProductSearchServiceImpl productSearchService;

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
    public ResponseEntity<List<SearchProductsRes>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        List<SearchProductsRes> results = productSearchService.searchProducts(name, category, minPrice, maxPrice, pageable);

        return ResponseEntity.ok(results);
    }
}
