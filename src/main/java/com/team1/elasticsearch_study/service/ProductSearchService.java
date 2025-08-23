package com.team1.elasticsearch_study.service;

import com.team1.elasticsearch_study.domain.product.Product;
import com.team1.elasticsearch_study.domain.product.ProductDocument;
import com.team1.elasticsearch_study.repository.ProductRepository;
import com.team1.elasticsearch_study.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private static final Logger log = LoggerFactory.getLogger(ProductSearchService.class);

    private final ProductSearchRepository productSearchRepository;
    private final ProductRepository productRepository;


    private static final int DEFAULT_BATCH_SIZE = 5000;

    @Async
    public void indexProductsFromDatabase() {
        log.info("데이터베이스에서 제품 색인 작업을 시작합니다. 배치 크기: {}", DEFAULT_BATCH_SIZE);
        
        int pageNum = 0;
        int totalProcessed = 0;
        
        try {
            while (true) {
                Page<Product> productPage = fetchProductPage(pageNum);
                
                if (productPage.isEmpty()) {
                    break;
                }

                // DB 에서 조회한 Product Entity를 ProductDocument로 변환
                List<ProductDocument> documents = convertToDocuments(productPage.getContent());
                productSearchRepository.saveAll(documents);
                
                totalProcessed += productPage.getNumberOfElements();
                log.debug("페이지 {} 처리 완료 ({} 개 문서)", pageNum, productPage.getNumberOfElements());
                pageNum++;
            }
            
            log.info("대량 색인 작업이 성공적으로 완료되었습니다. 총 문서 수: {}, 페이지 수: {}", 
                    totalProcessed, pageNum);
                    
        } catch (Exception e) {
            log.error("페이지 {}에서 대량 색인 작업이 실패했습니다: {}", pageNum, e.getMessage(), e);
            throw new RuntimeException("대량 색인 작업 실패", e);
        }
    }

    private Page<Product> fetchProductPage(int pageNum) {
        try {
            PageRequest pageRequest = PageRequest.of(pageNum, DEFAULT_BATCH_SIZE, Sort.by("id").ascending());
            return productRepository.findAll(pageRequest);
        } catch (DataAccessException e) {
            log.error("페이지 {}에서 데이터베이스 접근에 실패했습니다: {}", pageNum, e.getMessage());
            throw new RuntimeException("데이터베이스에서 제품 조회 실패", e);
        }
    }

    private List<ProductDocument> convertToDocuments(List<Product> products) {
        return products.stream()
                .map(Product::toDocument)
                .toList();
    }
}
