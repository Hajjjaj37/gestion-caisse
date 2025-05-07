package com.payment.controller;

import com.payment.model.Payment;
import com.payment.dto.*;
import com.payment.service.PaymentService;
import com.payment.exception.PaymentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
@Validated
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/single")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        Payment payment = paymentService.processPayment(request);
        return ResponseEntity.ok(convertToResponse(payment));
    }

    @PostMapping("/multi")
    public ResponseEntity<DetailedPaymentResponse> processMultiProductPayment(
            @Valid @RequestBody MultiProductPaymentRequest request) {
        DetailedPaymentResponse response = paymentService.processMultiProductPayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetailedPaymentResponse> getPayment(@PathVariable Long id) {
        DetailedPaymentResponse payment = paymentService.getDetailedPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentResponse> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Payment payment = paymentService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(convertToResponse(payment));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        List<PaymentResponse> responses = payments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    private PaymentResponse convertToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .productId(payment.getProductId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
} 