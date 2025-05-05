package com.Gestion.PromiereVersion.repository;

import com.Gestion.PromiereVersion.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    boolean existsByEmail(String email);
    boolean existsByTaxNumber(String taxNumber);
} 