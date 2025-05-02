package com.Gestion.PromiereVersion.repository;

import com.Gestion.PromiereVersion.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
} 