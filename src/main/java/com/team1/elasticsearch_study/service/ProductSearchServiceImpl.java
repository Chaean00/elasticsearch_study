package com.team1.elasticsearch_study.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.team1.elasticsearch_study.domain.product.ProductDocument;
import com.team1.elasticsearch_study.dto.SearchProductsRes;
import com.team1.elasticsearch_study.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSearchServiceImpl implements ProductSearchService {
    private final ProductSearchRepository productSearchRepository;
    private final ElasticsearchClient client;
    private final ApplicationEventPublisher eventPublisher;
    private static final String INDEX_NAME = "products";

    @Override
    public ProductDocument findById(Long id) {
        return productSearchRepository.findById(id).orElse(null);
    }

    @Override
    public List<ProductDocument> findAll(int pageNum) {
        return productSearchRepository.findAll(PageRequest.of(pageNum, 10, Sort.by("id").ascending())).getContent();
    }

    @Override
    public List<SearchProductsRes> searchProducts(String name, String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.info("Elasticsearch 검색 - name: {}, category: {}, minPrice: {}, maxPrice: {}, page: {}, size: {}",
                name, category, minPrice, maxPrice, pageable.getPageNumber(), pageable.getPageSize());

        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        boolean hasAnyCondition = false;

        // 이름: 텍스트 매칭 (부분/동의어/오타까지 고려하려면 analyzer/검색 타입 조정)
        if (name != null && !name.trim().isBlank()) {
            hasAnyCondition = true;
            boolQuery.must(m -> m
                    .matchPhrasePrefix(mm -> mm
                            .field("name")
                            .query(name.trim())
                            .maxExpansions(10)
                    )
            );
        }

        // 카테고리: 정확 일치가 일반적이므로 filter(term) 사용 (점수 영향 X)
        if (category != null && !category.trim().isBlank()) {
            hasAnyCondition = true;
            boolQuery.must(m -> m
                    .match(mm -> mm
                            .field("category")
                            .query(category)
                    )
            );
        }

        if (minPrice != null || maxPrice != null) {
            hasAnyCondition = true;
            boolQuery.filter(f -> f
                    .range(r -> r
                            .number(n -> {
                                        n.field("price");
                                        if (minPrice != null) n.gte(minPrice.doubleValue());
                                        if (maxPrice != null) n.lte(maxPrice.doubleValue());
                                        return n;
                            }
                            )
                    )
            );
        }

        Query query = hasAnyCondition
                ? Query.of(q -> q.bool(boolQuery.build()))
                : Query.of(q -> q.matchAll(m -> m));

        SortOptions sortIdAsc = SortOptions.of(s -> s
                .field(f -> f
                        .field("id")
                        .order(SortOrder.Asc)
                )
        );

        SearchRequest request = SearchRequest.of(s -> s
                .index(INDEX_NAME)
                        .query(query)
                        .from(pageable.getPageNumber() * pageable.getPageSize())
                        .size(pageable.getPageSize())
                        .sort(sortIdAsc)
                        .trackTotalHits(t -> t.enabled(true))
                );

        try {
            SearchResponse<ProductDocument> response = client.search(request, ProductDocument.class);
            return toResList(response);
        } catch (Exception e) {
            log.info("Elasticsearch 검색 오류: {}", e.getMessage());
            return List.of();
        }
    }

    private List<SearchProductsRes> toResList(SearchResponse<ProductDocument> response) {
        List<SearchProductsRes> list = new ArrayList<>();
        if (response.hits() == null || response.hits().hits() == null) return list;

        response.hits().hits().forEach(h -> {
            ProductDocument src = h.source();
            if (src == null) return;
            list.add(SearchProductsRes.builder()
                    .esId(src.getEsId())
                    .id(src.getId())
                    .name(src.getName())
                    .category(src.getCategory())
                    .price(src.getPrice())
                    .build());
        });
        return list;
    }
}
