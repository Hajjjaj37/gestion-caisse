package com.Gestion.PromiereVersion.repository;

import com.Gestion.PromiereVersion.model.Tax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxRepository extends JpaRepository<Tax, Long> {
    boolean existsByName(String name);
} 