package com.team1.elasticsearch_study.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.team1.elasticsearch_study.repository")
public class ElasticsearchConfig {

    // spring.elasticsearch.uris 프로퍼티를 application.yml에 설정했기 때문에,
    // Spring Boot가 Elasticsearch 클라이언트를 자동으로 구성합니다.
    // 이 클래스는 이제 리포지토리 스캔 경로를 지정하는 역할만 합니다.

}