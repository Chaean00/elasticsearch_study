package com.team1.elasticsearch_study.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Slf4j
@Configuration
@EnableElasticsearchRepositories // Spring Data Elasticsearch repository 활성화
public class ElasticsearchConfig{
}