package com.pamella.backend.service;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.pamella.backend.dto.ProductDTO;
import com.pamella.backend.dto.ProductIngredientDTO;
import com.pamella.backend.dto.RawMaterialDTO;
import com.pamella.backend.entity.Product;
import com.pamella.backend.entity.ProductIngredient;
import com.pamella.backend.entity.RawMaterial;
import com.pamella.backend.exception.ResourceNotFoundException;
import com.pamella.backend.repository.ProductRepository;
import com.pamella.backend.repository.RawMaterialRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;

    public List<ProductDTO> findAllDTO() {
        return productRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> filterDTO(String name, String code) {
        return productRepository.filter(name, code).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO findByIdDTO(Long id) {
        return toDTO(findById(id));
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    @Transactional
    public ProductDTO create(ProductDTO dto) {
        Product product = fromDTO(dto);
        return toDTO(productRepository.save(product));
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        Product existing = findById(id);
        existing.setCode(dto.getCode());
        existing.setName(dto.getName());
        existing.setPrice(dto.getPrice());

        List<ProductIngredient> existingIngredients = existing.getIngredients();
        if (existingIngredients == null) {
            existingIngredients = new ArrayList<>();
            existing.setIngredients(existingIngredients);
        }
        existingIngredients.clear();

        if (dto.getIngredients() != null) {
            for (ProductIngredientDTO ingDTO : dto.getIngredients()) {
                ProductIngredient ingredient = toIngredientEntity(ingDTO, existing);
                existingIngredients.add(ingredient);
            }
        }

        return toDTO(productRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        productRepository.delete(findById(id));
    }

    ProductDTO toDTO(Product product) {
        List<ProductIngredientDTO> ingredients = product.getIngredients() == null
                ? List.of()
                : product.getIngredients().stream()
                .map(this::toIngredientDTO)
                .collect(Collectors.toList());

        return new ProductDTO(
                product.getId(),
                product.getCode(),
                product.getName(),
                product.getPrice(),
                ingredients);
    }

    private ProductIngredientDTO toIngredientDTO(ProductIngredient ingredient) {
        RawMaterialDTO rmDTO = null;
        if (ingredient.getRawMaterial() != null) {
            RawMaterial rm = ingredient.getRawMaterial();
            rmDTO = new RawMaterialDTO(rm.getId(), rm.getCode(), rm.getName(), rm.getStockQuantity());
        }
        return new ProductIngredientDTO(ingredient.getId(), rmDTO, ingredient.getRequiredQuantity());
    }

    private Product fromDTO(ProductDTO dto) {
        Product product = new Product();
        product.setCode(dto.getCode());
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());

        List<ProductIngredient> ingredients = new ArrayList<>();
        if (dto.getIngredients() != null) {
            for (ProductIngredientDTO ingDTO : dto.getIngredients()) {
                ingredients.add(toIngredientEntity(ingDTO, product));
            }
        }
        product.setIngredients(ingredients);
        return product;
    }

    private ProductIngredient toIngredientEntity(ProductIngredientDTO dto, Product product) {
        ProductIngredient ingredient = new ProductIngredient();
        ingredient.setProduct(product);
        ingredient.setRequiredQuantity(dto.getRequiredQuantity());

        if (dto.getRawMaterial() != null && dto.getRawMaterial().getId() != null) {
            RawMaterial rm = rawMaterialRepository.findById(dto.getRawMaterial().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "RawMaterial not found: " + dto.getRawMaterial().getId()));
            ingredient.setRawMaterial(rm);
        }
        return ingredient;
    }
}