package com.Gestion.PromiereVersion.repository;

import com.Gestion.PromiereVersion.model.PaymentProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentProductRepository extends JpaRepository<PaymentProduct, Long> {
} 