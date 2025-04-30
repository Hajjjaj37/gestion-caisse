package com.Gestion.PromiereVersion.repository;

import com.Gestion.PromiereVersion.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);
    boolean existsByBarcode(String barcode);
    boolean existsByCodeBarre(String codeBarre);
    boolean existsByNameAndIdNot(String name, Long id);
    List<Product> findByIsVisibleTrue();
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByTaxId(Long taxId);
    Optional<Product> findByBarcode(String barcode);
    Optional<Product> findByCodeBarre(String codeBarre);
} 