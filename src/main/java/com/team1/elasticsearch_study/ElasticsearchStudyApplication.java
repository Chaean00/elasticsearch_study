package com.team1.elasticsearch_study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ElasticsearchStudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchStudyApplication.class, args);
    }

}
