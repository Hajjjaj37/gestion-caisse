package com.Gestion.PromiereVersion.controller;

import com.Gestion.PromiereVersion.dto.ErrorResponse;
import com.Gestion.PromiereVersion.dto.OrderRequestDTO;
import com.Gestion.PromiereVersion.dto.OrderResponseDTO;
import com.Gestion.PromiereVersion.model.Order;
import com.Gestion.PromiereVersion.model.Payment;
import com.Gestion.PromiereVersion.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDTO orderRequest) {
        try {
            log.info("Création d'une nouvelle commande pour le client: {}", orderRequest.getCustomerId());
            Order order = orderService.createOrder(orderRequest);
            return ResponseEntity.ok(OrderResponseDTO.fromOrder(order));
        } catch (Exception e) {
            log.error("Erreur lors de la création de la commande", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.builder()
                            .message(e.getMessage())
                            .error("Internal Server Error")
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<?> getOrder(@PathVariable Long id) {
        try {
            log.info("Récupération de la commande: {}", id);
            Order order = orderService.getOrderById(id);
            return ResponseEntity.ok(OrderResponseDTO.fromOrder(order));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de la commande", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.builder()
                            .message(e.getMessage())
                            .error("Internal Server Error")
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }

    @PostMapping("/{orderId}/pay")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<?> processPayment(@PathVariable Long orderId) {
        try {
            log.info("Traitement du paiement pour la commande: {}", orderId);
            Payment payment = orderService.processPayment(orderId);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            log.error("Erreur lors du traitement du paiement", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.builder()
                            .message(e.getMessage())
                            .error("Internal Server Error")
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }
} 