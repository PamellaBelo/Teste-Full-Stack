package com.pamella.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductionSuggestionDTO {
    private List<SuggestedProduct> suggestedProducts;
    private BigDecimal totalExpectedRevenue;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuggestedProduct {
        private String productName;
        private int quantityToProduce;
        private BigDecimal totalRevenue;
    }
}



