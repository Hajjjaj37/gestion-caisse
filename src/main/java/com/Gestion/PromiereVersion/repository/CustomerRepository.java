package com.Gestion.PromiereVersion.repository;

import com.Gestion.PromiereVersion.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByCustomerCardNumber(String customerCardNumber);
    boolean existsByEmail(String email);
    boolean existsByCustomerCardNumber(String customerCardNumber);
} 