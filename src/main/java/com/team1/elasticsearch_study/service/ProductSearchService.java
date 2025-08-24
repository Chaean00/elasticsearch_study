package com.team1.elasticsearch_study.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.team1.elasticsearch_study.domain.product.ProductDocument;
import com.team1.elasticsearch_study.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSearchService {
    private final ProductSearchRepository productSearchRepository;
    private final ElasticsearchClient client;
    private static final String INDEX_NAME = "products";

    public ProductDocument findById(Long id) {
        return productSearchRepository.findById(id).orElse(null);
    }

    public List<ProductDocument> findAll(int pageNum) {
        return productSearchRepository.findAll(PageRequest.of(pageNum, 10, Sort.by("id").ascending())).getContent();
    }

    public List<ProductDocument> findByProductsByName(String name) {
        try {
            log.info("이름 검색 : name={}", name);
            SearchResponse<ProductDocument> response = client.search(s -> s
                            .index(INDEX_NAME)
                            .query(q -> q
                                    .match(m -> m
                                            .field("name")
                                            .query(name)
                                    )
                            ),
                    ProductDocument.class
            );
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();
        } catch (IOException e) {
            log.error("Elasticsearch 쿼리 실패: {}", name, e);
            throw  new RuntimeException("이름 검색에 실패하였습니다.", e);
        }
    }

    public List<ProductDocument> findByProductsByPrice(BigDecimal min, BigDecimal max) {
        try {
            log.info("가격 범위 검색 : min={}, max={}", min, max);
            SearchResponse<ProductDocument> response = client.search(s -> s
                            .index(INDEX_NAME)
                            .query(q -> q
                                    .range(r -> r
                                            .number(n -> {
                                                n.field("price");
                                                if (min != null) n.gte(min.doubleValue());
                                                if (max != null) n.lte(max.doubleValue());
                                                return n;
                                            }))),
                    ProductDocument.class
            );
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Elasticsearch 쿼리 실패: min={}, max={}", min, max, e);
            throw new RuntimeException("이름 검색에 실패하였습니다.", e);
        }
    }

    public List<ProductDocument> findByProductsCategory(String category, int pageNum) {

        Pageable pageable = PageRequest.of(pageNum, 10, Sort.by("id").ascending());
        return productSearchRepository.findByCategory(category, pageable);
    }
}
