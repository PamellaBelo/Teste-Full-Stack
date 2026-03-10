package com.pamella.backend.controller;

import com.pamella.backend.dto.ProductionSuggestionDTO;
import com.pamella.backend.service.ProductionOptimizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/production")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProductionOptimizationController {

    private final ProductionOptimizationService service;

    @GetMapping("/optimize")
    public ResponseEntity<ProductionSuggestionDTO> optimize() {
        ProductionSuggestionDTO suggestion = service.suggestProduction();
        return ResponseEntity.ok(suggestion);
    }
}