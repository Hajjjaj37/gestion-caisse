package com.Gestion.PromiereVersion.controller;

import com.Gestion.PromiereVersion.dto.OrderResponseDTO;
import com.Gestion.PromiereVersion.dto.PaymentRequestDTO;
import com.Gestion.PromiereVersion.model.Order;
import com.Gestion.PromiereVersion.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<OrderResponseDTO> processPayment(@RequestBody PaymentRequestDTO paymentRequest) {
        Order order = paymentService.processPayment(paymentRequest);
        return ResponseEntity.ok(OrderResponseDTO.fromOrder(order));
    }
} 