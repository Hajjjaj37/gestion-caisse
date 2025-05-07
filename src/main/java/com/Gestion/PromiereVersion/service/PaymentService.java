package com.Gestion.PromiereVersion.service;

import com.Gestion.PromiereVersion.dto.PaymentRequest;
import com.Gestion.PromiereVersion.dto.PaymentResponse;
import com.Gestion.PromiereVersion.dto.PaymentProductDTO;
import com.Gestion.PromiereVersion.dto.PaymentRequestDTO;
import com.Gestion.PromiereVersion.model.*;
import com.Gestion.PromiereVersion.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request);
    PaymentResponse getPaymentById(Long id);
    PaymentResponse updatePaymentStatus(Long id, String status);
} 