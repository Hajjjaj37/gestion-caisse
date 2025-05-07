package com.Gestion.PromiereVersion.repository;

import com.Gestion.PromiereVersion.model.Order;
import com.Gestion.PromiereVersion.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByPayment(Payment payment);
} 