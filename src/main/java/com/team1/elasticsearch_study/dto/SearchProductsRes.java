package com.team1.elasticsearch_study.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchProductsRes {
    private String esId;
    private Long id;
    private String name;
    private BigDecimal price;
    private String category;
}
