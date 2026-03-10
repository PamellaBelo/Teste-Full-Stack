package com.pamella.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import com.pamella.backend.dto.RawMaterialDTO;
import com.pamella.backend.entity.RawMaterial;
import com.pamella.backend.exception.ResourceNotFoundException;
import com.pamella.backend.repository.RawMaterialRepository;
import org.springframework.stereotype.Service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RawMaterialService {

    private final RawMaterialRepository repository;

    public List<RawMaterialDTO> findAllDTO() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<RawMaterialDTO> filterDTO(String name, String code) {
        if (name != null && !name.isBlank() && code != null && !code.isBlank()) {
            return toDTO(repository.findByNameContainingIgnoreCaseAndCodeContainingIgnoreCase(name, code));
        } else if (name != null && !name.isBlank()) {
            return toDTO(repository.findByNameContainingIgnoreCase(name));
        } else if (code != null && !code.isBlank()) {
            return toDTO(repository.findByCodeContainingIgnoreCase(code));
        }
        return findAllDTO();
    }

    public RawMaterialDTO findByIdDTO(Long id) {
        return toDTO(findById(id));
    }

    public RawMaterial findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RawMaterial not found: " + id));
    }

    @Transactional
    public RawMaterialDTO create(RawMaterialDTO dto) {
        return toDTO(repository.save(fromDTO(dto)));
    }

    @Transactional
    public RawMaterialDTO update(Long id, RawMaterialDTO dto) {
        RawMaterial existing = findById(id);
        existing.setCode(dto.getCode());
        existing.setName(dto.getName());
        existing.setStockQuantity(dto.getStockQuantity());
        return toDTO(repository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    RawMaterialDTO toDTO(RawMaterial entity) {
        return new RawMaterialDTO(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getStockQuantity());
    }

    private List<RawMaterialDTO> toDTO(List<RawMaterial> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private RawMaterial fromDTO(RawMaterialDTO dto) {
        RawMaterial entity = new RawMaterial();
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setStockQuantity(dto.getStockQuantity());
        return entity;
    }
}