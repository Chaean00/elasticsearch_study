package com.team1.elasticsearch_study.service;

import com.team1.elasticsearch_study.domain.product.Product;
import com.team1.elasticsearch_study.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductSearchRepository productSearchRepository;


}