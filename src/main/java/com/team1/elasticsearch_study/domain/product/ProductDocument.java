package com.team1.elasticsearch_study.domain.product;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "products")
public class ProductDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text, name = "name")
    private String name;

    @Field(type = FieldType.Integer, name = "price")
    private Integer price;

    @Field(type = FieldType.Keyword, name = "category")
    private String category;

    @Field(type = FieldType.Date, name = "created_at")
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date, name = "updated_at")
    private LocalDateTime updatedAt;
}