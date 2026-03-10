package com.pamella.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pamella.backend.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByCode(String code);

    Product findByCode(String code);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByCodeContainingIgnoreCase(String code);

    List<Product> findByNameContainingIgnoreCaseAndCodeContainingIgnoreCase(String name, String code);

    default List<Product> filter(String name, String code) {
        if (name != null && !name.isBlank() && code != null && !code.isBlank()) {
            return findByNameContainingIgnoreCaseAndCodeContainingIgnoreCase(name, code);
        }
        if (name != null && !name.isBlank()) {
            return findByNameContainingIgnoreCase(name);
        }
        if (code != null && !code.isBlank()) {
            return findByCodeContainingIgnoreCase(code);
        }
        return findAll();
    }
}