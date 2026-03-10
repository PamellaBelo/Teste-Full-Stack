package com.pamella.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ProductIngredientDTO {

    private Long id;

    @NotNull(message = "Raw material is required")
    private RawMaterialDTO rawMaterial;

    @NotNull(message = "Required quantity is required")
    @Positive(message = "Required quantity must be positive")
    private Double requiredQuantity;
}