package com.Gestion.PromiereVersion.service;

import com.Gestion.PromiereVersion.dto.PaymentRequestDTO;
import com.Gestion.PromiereVersion.model.*;
import com.Gestion.PromiereVersion.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order processPayment(PaymentRequestDTO paymentRequest) {
        log.info("Début du traitement du paiement");

        // Vérifier le client
        Customer customer = customerRepository.findById(paymentRequest.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Client non trouvé"));

        // Créer la commande d'abord
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .customer(customer)
                .status("COMPLETED")
                .paymentStatus("PAID")
                .paymentMethod(paymentRequest.getPaymentMethod())
                .notes(paymentRequest.getNotes())
                .totalAmount(paymentRequest.getTotalAmount())
                .build();

        // Sauvegarder la commande
        Order savedOrder = orderRepository.save(order);
        log.info("Commande créée avec l'ID: {}", savedOrder.getId());

        // Créer et sauvegarder le paiement avec la référence à la commande
        Payment payment = Payment.builder()
                .order(savedOrder)
                .amount(paymentRequest.getTotalAmount())
                .paymentMethod(paymentRequest.getPaymentMethod())
                .status("PAID")
                .paymentDate(LocalDateTime.now())
                .notes(paymentRequest.getNotes())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Paiement créé avec l'ID: {}", savedPayment.getId());

        // Mettre à jour la commande avec le paiement
        savedOrder.setPayment(savedPayment);
        orderRepository.save(savedOrder);

        // Créer et sauvegarder les éléments de commande
        List<OrderItem> orderItems = paymentRequest.getItems().stream()
                .map(item -> {
                    Product product = productRepository.findById(item.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));

                    if (product.getStock() < item.getQuantity()) {
                        throw new IllegalArgumentException("Stock insuffisant pour le produit: " + product.getName());
                    }

                    OrderItem orderItem = OrderItem.builder()
                            .order(savedOrder)
                            .product(product)
                            .quantity(item.getQuantity())
                            .unitPrice(item.getUnitPrice())
                            .totalPrice(item.getTotalPrice())
                            .build();

                    // Sauvegarder l'élément de commande
                    OrderItem savedItem = orderItemRepository.save(orderItem);
                    log.info("Élément de commande créé avec l'ID: {}", savedItem.getId());

                    // Mettre à jour le stock
                    product.setStock(product.getStock() - item.getQuantity());
                    productRepository.save(product);
                    log.info("Stock mis à jour pour le produit: {}", product.getId());

                    return savedItem;
                })
                .collect(Collectors.toList());

        // Mettre à jour la commande avec les éléments
        savedOrder.setOrderItems(orderItems);
        Order finalOrder = orderRepository.save(savedOrder);
        log.info("Commande mise à jour avec les éléments");

        return finalOrder;
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
} 