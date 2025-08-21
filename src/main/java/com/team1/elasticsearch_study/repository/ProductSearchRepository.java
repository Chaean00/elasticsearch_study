package com.team1.elasticsearch_study.repository;

import com.team1.elasticsearch_study.domain.product.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {
}