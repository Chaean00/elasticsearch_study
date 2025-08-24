package com.team1.elasticsearch_study.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.util.FileUtil;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.nio.file.Path;

@Slf4j
@Component
@RequiredArgsConstructor
public class IndexBootstrap {
    private final ElasticsearchClient es;
    private static final String INDEX_NAME = "products";

    @EventListener(ApplicationReadyEvent.class)
    public void init() throws Exception {
        if (es.indices().exists(e -> e.index(INDEX_NAME)).value()) {
            log.info(INDEX_NAME + " 인덱스가 이미 존재합니다.");
            return;
        }
        try (StringReader settings = new StringReader(FileUtil.readAsString(Path.of("src/main/resources/es/products-settings.json").toFile()));
             StringReader mappings = new StringReader(FileUtil.readAsString(Path.of("src/main/resources/es/products-mappings.json").toFile()))){

            es.indices().create(c -> c.index(INDEX_NAME)
                    .settings(s -> s.withJson(settings))
                    .mappings(m -> m.withJson(mappings)));

            log.info(INDEX_NAME + " 인덱스 생성 완료");
        }
    }
}
