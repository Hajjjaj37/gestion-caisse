package com.Gestion.PromiereVersion.repository;

import com.Gestion.PromiereVersion.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findByEmail(String email);
    boolean existsByEmail(String email);
} 