package com.Gestion.PromiereVersion.service;

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

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final PaymentProductRepository paymentProductRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public Order processPayment(PaymentRequestDTO paymentRequest) {
        // Vérifier le client
        Customer customer = customerRepository.findById(paymentRequest.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Créer le paiement
        Payment payment = Payment.builder()
                .customer(customer)
                .amount(paymentRequest.getAmount())
                .paymentMethod(paymentRequest.getPaymentMethod())
                .status(PaymentStatus.COMPLETED)
                .transactionId(UUID.randomUUID().toString())
                .build();

        // Sauvegarder le paiement
        Payment savedPayment = paymentRepository.save(payment);

        // Créer la commande
        Order order = Order.builder()
                .customer(customer)
                .payment(savedPayment)
                .totalAmount(paymentRequest.getAmount())
                .status(OrderStatus.COMPLETED)
                .items(new ArrayList<>())
                .build();

        // Sauvegarder la commande
        Order savedOrder = orderRepository.save(order);

        // Traiter les produits
        if (paymentRequest.getProducts() != null) {
            for (PaymentProductDTO productDTO : paymentRequest.getProducts()) {
                // Vérifier le produit
                Product product = productRepository.findById(productDTO.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found with id: " + productDTO.getProductId()));

                // Vérifier le stock
                if (product.getStock() < productDTO.getQuantity()) {
                    throw new RuntimeException("Insufficient stock for product: " + product.getName());
                }

                // Calculer le prix total pour l'article
                BigDecimal totalPrice = productDTO.getUnitPrice().multiply(BigDecimal.valueOf(productDTO.getQuantity()));

                // Créer l'article de commande
                OrderItem orderItem = OrderItem.builder()
                        .order(savedOrder)
                        .product(product)
                        .quantity(productDTO.getQuantity())
                        .unitPrice(productDTO.getUnitPrice())
                        .totalPrice(totalPrice)
                        .build();

                // Sauvegarder l'article de commande
                orderItemRepository.save(orderItem);
                savedOrder.getItems().add(orderItem);

                // Mettre à jour le stock
                product.setStock(product.getStock() - productDTO.getQuantity());
                productRepository.save(product);
            }
        }

        return savedOrder;
    }
} 