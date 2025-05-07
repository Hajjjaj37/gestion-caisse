package com.payment.service.impl;

import com.payment.model.Payment;
import com.payment.dto.*;
import com.payment.service.PaymentService;
import com.payment.repository.PaymentRepository;
import com.payment.exception.PaymentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    @Transactional
    public Payment processPayment(PaymentRequest request) {
        Payment payment = new Payment();
        payment.setProductId(request.getProductId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus("PENDING");
        
        // Here you would typically integrate with a payment gateway
        // For now, we'll just save the payment
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public DetailedPaymentResponse processMultiProductPayment(MultiProductPaymentRequest request) {
        // Calculate total amount
        BigDecimal totalAmount = request.getProducts().stream()
                .map(product -> product.getUnitPrice().multiply(BigDecimal.valueOf(product.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create and save payment
        Payment payment = new Payment();
        payment.setProductId("MULTI"); // Special identifier for multi-product payments
        payment.setAmount(totalAmount);
        payment.setCurrency(request.getCurrency());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus("PENDING");
        
        Payment savedPayment = paymentRepository.save(payment);

        // Create detailed response
        return DetailedPaymentResponse.builder()
                .id(savedPayment.getId())
                .products(request.getProducts())
                .totalAmount(totalAmount)
                .currency(request.getCurrency())
                .paymentMethod(request.getPaymentMethod())
                .status(savedPayment.getStatus())
                .createdAt(savedPayment.getCreatedAt())
                .updatedAt(savedPayment.getUpdatedAt())
                .build();
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentException("Payment not found with id: " + id));
    }

    @Override
    public DetailedPaymentResponse getDetailedPaymentById(Long id) {
        Payment payment = getPaymentById(id);
        // In a real application, you would fetch the product details from a separate table
        // For now, we'll return a simplified response
        return DetailedPaymentResponse.builder()
                .id(payment.getId())
                .totalAmount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public Payment updatePaymentStatus(Long id, String status) {
        Payment payment = getPaymentById(id);
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
} 