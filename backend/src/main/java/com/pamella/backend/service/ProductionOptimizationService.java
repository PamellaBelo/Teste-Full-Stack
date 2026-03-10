package com.pamella.backend.service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pamella.backend.dto.ProductionSuggestionDTO;
import com.pamella.backend.entity.Product;
import com.pamella.backend.entity.ProductIngredient;
import com.pamella.backend.repository.ProductRepository;
import com.pamella.backend.repository.RawMaterialRepository;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductionOptimizationService {

    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;

    public ProductionSuggestionDTO suggestProduction() {
        List<Product> products = productRepository.findAll();
        Map<Long, Double> currentStock = buildStockMap();

        List<ProductPotential> potentials = calculatePotentials(products, currentStock);
        potentials.sort(Comparator.comparing(ProductPotential::getRevenuePerUnit).reversed());

        return simulateProduction(potentials, currentStock);
    }

    Map<Long, Double> buildStockMap() {
        Map<Long, Double> stock = new HashMap<>();
        rawMaterialRepository.findAll()
                .forEach(rm -> stock.put(rm.getId(), rm.getStockQuantity()));
        return stock;
    }

    List<ProductPotential> calculatePotentials(List<Product> products, Map<Long, Double> stock) {
        List<ProductPotential> potentials = new ArrayList<>();
        for (Product product : products) {
            int maxUnits = maxUnitsPossible(product, stock);
            if (maxUnits > 0) {
                potentials.add(new ProductPotential(product, maxUnits, product.getPrice()));
            }
        }
        return potentials;
    }

    private ProductionSuggestionDTO simulateProduction(
            List<ProductPotential> potentials, Map<Long, Double> initialStock) {

        Map<Long, Double> simulatedStock = new HashMap<>(initialStock);
        List<ProductionSuggestionDTO.SuggestedProduct> suggestions = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (ProductPotential potential : potentials) {
            int possible = maxUnitsPossible(potential.product, simulatedStock);
            if (possible <= 0) continue;

            deductStock(potential.product, possible, simulatedStock);

            BigDecimal revenue = potential.product.getPrice().multiply(BigDecimal.valueOf(possible));
            suggestions.add(new ProductionSuggestionDTO.SuggestedProduct(
                    potential.product.getName(), possible, revenue));
            totalRevenue = totalRevenue.add(revenue);
        }

        return new ProductionSuggestionDTO(suggestions, totalRevenue);
    }

    int maxUnitsPossible(Product product, Map<Long, Double> stock) {
        List<ProductIngredient> ingredients = product.getIngredients();
        if (ingredients == null || ingredients.isEmpty()) return 0;

        int max = Integer.MAX_VALUE;
        for (ProductIngredient ingredient : ingredients) {
            Long materialId = ingredient.getRawMaterial().getId();
            Double available = stock.get(materialId);
            Double required = ingredient.getRequiredQuantity();

            if (available == null || available <= 0 || required == null || required <= 0) return 0;

            int possible = (int) Math.floor(available / required);
            if (possible < max) max = possible;
        }
        return max == Integer.MAX_VALUE ? 0 : max;
    }

    private void deductStock(Product product, int units, Map<Long, Double> stock) {
        for (ProductIngredient ingredient : product.getIngredients()) {
            Long materialId = ingredient.getRawMaterial().getId();
            double consumed = ingredient.getRequiredQuantity() * units;
            stock.merge(materialId, -consumed, Double::sum);
        }
    }

    static class ProductPotential {
        final Product product;
        final int maxUnits;
        final BigDecimal revenuePerUnit;

        ProductPotential(Product product, int maxUnits, BigDecimal revenuePerUnit) {
            this.product = product;
            this.maxUnits = maxUnits;
            this.revenuePerUnit = revenuePerUnit;
        }

        public BigDecimal getRevenuePerUnit() {
            return revenuePerUnit;
        }
    }
}