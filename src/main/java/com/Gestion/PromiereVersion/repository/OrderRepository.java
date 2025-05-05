package com.Gestion.PromiereVersion.repository;

import com.Gestion.PromiereVersion.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
} 