package com.payment.service;

import com.payment.model.Payment;
import com.payment.dto.PaymentRequest;
import com.payment.dto.MultiProductPaymentRequest;
import com.payment.dto.DetailedPaymentResponse;
import java.util.List;

public interface PaymentService {
    Payment processPayment(PaymentRequest request);
    DetailedPaymentResponse processMultiProductPayment(MultiProductPaymentRequest request);
    Payment getPaymentById(Long id);
    DetailedPaymentResponse getDetailedPaymentById(Long id);
    Payment updatePaymentStatus(Long id, String status);
    List<Payment> getAllPayments();
} 