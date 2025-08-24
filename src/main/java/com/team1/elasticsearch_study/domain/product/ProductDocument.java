package com.team1.elasticsearch_study.domain.product;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "products", createIndex = false)
@Builder
public class ProductDocument {
    @Id
    private String esId;
    @Field(type = FieldType.Long, name = "id")
    private Long id;
    @Field(type = FieldType.Text, name = "name", analyzer = "korean_search", searchAnalyzer = "korean_search")
    private String name;
    @Field(type = FieldType.Scaled_Float, name = "price", scalingFactor = 100)
    private BigDecimal price;
    @Field(type = FieldType.Keyword, name = "category")
    private String category;
    @Field(type = FieldType.Date, name = "created_at",
            format = DateFormat.date_hour_minute_second_millis, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;
    @Field(type = FieldType.Date, name = "updated_at",
            format = DateFormat.date_hour_minute_second_millis, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime updatedAt;
}