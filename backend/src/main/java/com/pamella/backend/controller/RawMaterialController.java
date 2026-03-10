package com.pamella.backend.controller;

import java.util.List;

import com.pamella.backend.dto.RawMaterialDTO;
import com.pamella.backend.service.RawMaterialService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/raw-materials")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RawMaterialController {

    private final RawMaterialService service;

    @GetMapping
    public List<RawMaterialDTO> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code) {
        if ((name != null && !name.isBlank()) || (code != null && !code.isBlank())) {
            return service.filterDTO(name, code);
        }
        return service.findAllDTO();
    }

    @GetMapping("/{id}")
    public RawMaterialDTO findById(@PathVariable Long id) {
        return service.findByIdDTO(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RawMaterialDTO create(@Valid @RequestBody RawMaterialDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public RawMaterialDTO update(@PathVariable Long id, @Valid @RequestBody RawMaterialDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}