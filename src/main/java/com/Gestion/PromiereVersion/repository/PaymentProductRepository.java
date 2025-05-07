package com.Gestion.PromiereVersion.repository;

import com.Gestion.PromiereVersion.model.Payment;
import com.Gestion.PromiereVersion.model.PaymentProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentProductRepository extends JpaRepository<PaymentProduct, Long> {
    List<PaymentProduct> findByPayment(Payment payment);
} 