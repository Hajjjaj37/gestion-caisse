package com.Gestion.PromiereVersion.service;

import com.Gestion.PromiereVersion.dto.OrderRequestDTO;
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
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order createOrder(OrderRequestDTO orderRequest) {
        log.info("Début de la création de la commande");
        
        // Récupérer le client
        Customer customer = customerRepository.findById(orderRequest.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Client non trouvé"));

        // Calculer le montant total
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderRequestDTO.OrderItemRequestDTO item : orderRequest.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));

            if (product.getStock() < item.getQuantity()) {
                throw new IllegalArgumentException("Stock insuffisant pour le produit: " + product.getName());
            }

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        // Créer et sauvegarder le paiement d'abord
        Payment payment = Payment.builder()
                .amount(totalAmount)
                .paymentMethod(orderRequest.getPaymentMethod())
                .status("PENDING")
                .paymentDate(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Paiement créé avec l'ID: {}", savedPayment.getId());

        // Créer la commande
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .customer(customer)
                .status("PENDING")
                .paymentStatus("PENDING")
                .paymentMethod(orderRequest.getPaymentMethod())
                .notes(orderRequest.getNotes())
                .totalAmount(totalAmount)
                .payment(savedPayment)
                .build();

        // Sauvegarder la commande
        Order savedOrder = orderRepository.save(order);
        log.info("Commande créée avec l'ID: {}", savedOrder.getId());

        // Créer et sauvegarder les éléments de commande
        List<OrderItem> orderItems = orderRequest.getItems().stream()
                .map(item -> {
                    Product product = productRepository.findById(item.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));

                    BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

                    OrderItem orderItem = OrderItem.builder()
                            .order(savedOrder)
                            .product(product)
                            .quantity(item.getQuantity())
                            .unitPrice(product.getPrice())
                            .totalPrice(itemTotal)
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

    @Transactional
    public Payment processPayment(Long orderId) {
        log.info("Traitement du paiement pour la commande: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée"));

        if (!"PENDING".equals(order.getPaymentStatus())) {
            throw new IllegalArgumentException("Le paiement a déjà été traité");
        }

        // Mettre à jour le paiement
        Payment payment = order.getPayment();
        payment.setStatus("PAID");
        payment.setPaymentDate(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Paiement mis à jour avec l'ID: {}", savedPayment.getId());

        // Mettre à jour la commande
        order.setPaymentStatus("PAID");
        Order updatedOrder = orderRepository.save(order);
        log.info("Commande mise à jour avec le statut de paiement: PAID");

        return savedPayment;
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée"));
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
} 