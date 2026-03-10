package com.pamella.backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.pamella.backend.entity.RawMaterial;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {

    boolean existsByCode(String code);

    RawMaterial findByCode(String code);

    List<RawMaterial> findByNameContainingIgnoreCase(String name);

    List<RawMaterial> findByCodeContainingIgnoreCase(String code);

    List<RawMaterial> findByNameContainingIgnoreCaseAndCodeContainingIgnoreCase(String name, String code);

}