package com.team1.elasticsearch_study.service;

import com.team1.elasticsearch_study.domain.product.Product;
import com.team1.elasticsearch_study.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSearchIndexService {

    private final ProductRepository productRepository;
    private final ElasticsearchOperations elasticsearchOperations;


    private static final int DEFAULT_BATCH_SIZE = 5000;
    private final String INDEX_NAME = "products";

    @Async
    public void indexProductsFromDatabase() {
        long startTime = System.currentTimeMillis();
        log.info("색인 작업 시작 - 병렬 처리 모드");
        
        try {
            // 데이터 전체 개수
            long totalCount = productRepository.getTotalProductCount();
            if (totalCount == 0) {
                log.info("색인할 데이터가 존재하지않습니다.");
                return;
            }

            // 쓰레드 수 최대 4개로 제한
            int threads = Math.min(Runtime.getRuntime().availableProcessors(), 4);
            // 각 스레드가 처리할 ID 범위 크기 계산 : 100만 / CPU 수
            long rangeSize = totalCount / threads;
            
            log.info("총 제품 수: {}, 병렬 스레드: {}, 범위당 크기: {}", totalCount, threads, rangeSize);
            
            // 병렬 처리를 위한 ID 범위 분할
            List<CompletableFuture<Integer>> futures = IntStream.range(0, threads)
                .mapToObj(i -> {
                    long startId = i * rangeSize + 1;
                    long endId = (i == threads - 1) ? Long.MAX_VALUE : (i + 1) * rangeSize;
                    return indexProductRangeAsync(startId, endId, i);
                })
                .toList();
            
            // 모든 병렬 작업 완료 대기
            CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
            );
            
            int totalProcessed = allTasks.thenApply(v -> 
                futures.stream().mapToInt(CompletableFuture::join).sum()
            ).join();
            
            log.info("색인 완료. 총 문서: {}, 소요시간: {}ms",
                    totalProcessed, System.currentTimeMillis() - startTime);
                    
        } catch (Exception e) {
            log.error("색인 작업 실패: {}", e.getMessage(), e);
            throw new RuntimeException("고성능 색인 작업 실패", e);
        }
    }
    
    private CompletableFuture<Integer> indexProductRangeAsync(long startId, long endId, int threadId) {
        return CompletableFuture.supplyAsync(() -> {
            log.debug("스레드 {} 시작: ID 범위 {}-{}", threadId, startId, endId);
            
            int processed = 0;
            Long lastId = startId - 1;
            
            try {
                while (true) {
                    List<Product> products = fetchProductsAfterCursor(lastId);
                    
                    if (products.isEmpty()) {
                        break;
                    }
                    
                    // ID 범위 체크
                    if (products.get(0).getId() > endId) {
                        break;
                    }
                    
                    List<IndexQuery> indexQueries = createIndexQuery(products);
                    bulkIndexToElasticsearch(indexQueries);
                    
                    processed += products.size();
                    lastId = products.get(products.size() - 1).getId();
                    
                    log.debug("스레드 {} - {} 개 문서 처리 완료 (마지막 ID: {})", threadId, products.size(), lastId);
                }
                
                log.info("스레드 {} 완료: {} 개 문서 처리", threadId, processed);
                return processed;
                
            } catch (Exception e) {
                log.error("스레드 {} 실패: {}", threadId, e.getMessage(), e);
                throw new RuntimeException("스레드 " + threadId + " 색인 실패", e);
            }
        }, ForkJoinPool.commonPool());
    }
    
    private List<Product> fetchProductsAfterCursor(Long lastId) {
        try {
            PageRequest pageRequest = PageRequest.of(0, DEFAULT_BATCH_SIZE);
            return productRepository.findProductsAfterIdWithLimit(lastId, pageRequest);
        } catch (DataAccessException e) {
            log.error("커서 ID {} 이후 제품 조회 실패: {}", lastId, e.getMessage());
            throw new RuntimeException("커서 기반 제품 조회 실패", e);
        }
    }

    private List<IndexQuery> createIndexQuery(List<Product> products) {
        return products.stream()
                .map(product -> new IndexQueryBuilder()
                        .withId(product.getId().toString())
                        .withObject(product.toDocument())
                        .build()
                )
                .toList();
    }

    private void bulkIndexToElasticsearch(List<IndexQuery> indexQueries) {
        try {
            elasticsearchOperations.bulkIndex(indexQueries, IndexCoordinates.of(INDEX_NAME));
        } catch (Exception e) {
            log.error("Elasticsearch에 색인 작업 실패: {}", e.getMessage(), e);
            throw new RuntimeException("Elasticsearch 색인 작업 실패", e);
        }
    }
}
