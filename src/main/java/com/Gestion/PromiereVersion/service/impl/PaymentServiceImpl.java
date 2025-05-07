package com.Gestion.PromiereVersion.service.impl;

import com.Gestion.PromiereVersion.dto.PaymentRequest;
import com.Gestion.PromiereVersion.dto.PaymentResponse;
import com.Gestion.PromiereVersion.model.*;
import com.Gestion.PromiereVersion.repository.*;
import com.Gestion.PromiereVersion.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final PaymentProductRepository paymentProductRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        // Vérifier le client
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Créer le paiement
        Payment payment = Payment.builder()
                .customer(customer)
                .amount(calculateTotalAmount(request.getProducts()))
                .paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()))
                .status(PaymentStatus.PENDING)
                .transactionId(UUID.randomUUID().toString())
                .build();

        // Sauvegarder le paiement
        Payment savedPayment = paymentRepository.save(payment);

        // Créer la commande
        Order order = Order.builder()
                .customer(customer)
                .payment(savedPayment)
                .totalAmount(savedPayment.getAmount())
                .status(OrderStatus.PENDING)
                .items(new ArrayList<>())
                .build();

        // Sauvegarder la commande
        Order savedOrder = orderRepository.save(order);

        // Traiter les produits
        if (request.getProducts() != null) {
            for (PaymentRequest.ProductDetail productDetail : request.getProducts()) {
                // Vérifier le produit
                Product product = productRepository.findById(Long.parseLong(productDetail.getProductId()))
                        .orElseThrow(() -> new RuntimeException("Product not found with id: " + productDetail.getProductId()));

                // Vérifier le stock
                if (product.getStock() < productDetail.getQuantity()) {
                    throw new RuntimeException("Insufficient stock for product: " + product.getName());
                }

                // Créer le PaymentProduct
                PaymentProduct paymentProduct = PaymentProduct.builder()
                        .payment(savedPayment)
                        .product(product)
                        .quantity(productDetail.getQuantity())
                        .unitPrice(BigDecimal.valueOf(productDetail.getUnitPrice()))
                        .build();

                // Sauvegarder le PaymentProduct
                paymentProductRepository.save(paymentProduct);

                // Calculer le prix total pour l'article
                BigDecimal totalPrice = BigDecimal.valueOf(productDetail.getUnitPrice())
                        .multiply(BigDecimal.valueOf(productDetail.getQuantity()));

                // Créer l'article de commande
                OrderItem orderItem = OrderItem.builder()
                        .order(savedOrder)
                        .product(product)
                        .quantity(productDetail.getQuantity())
                        .unitPrice(BigDecimal.valueOf(productDetail.getUnitPrice()))
                        .totalPrice(totalPrice)
                        .build();

                // Sauvegarder l'article de commande
                orderItemRepository.save(orderItem);
                savedOrder.getItems().add(orderItem);

                // Mettre à jour le stock
                product.setStock(product.getStock() - productDetail.getQuantity());
                productRepository.save(product);
            }
        }

        return convertToResponse(savedPayment);
    }

    @Override
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        return convertToResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse updatePaymentStatus(Long id, String status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        payment.setStatus(PaymentStatus.valueOf(status));
        Payment updatedPayment = paymentRepository.save(payment);
        return convertToResponse(updatedPayment);
    }

    private BigDecimal calculateTotalAmount(List<PaymentRequest.ProductDetail> products) {
        return products.stream()
                .map(product -> BigDecimal.valueOf(product.getUnitPrice())
                        .multiply(BigDecimal.valueOf(product.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private PaymentResponse convertToResponse(Payment payment) {
        // Récupérer les PaymentProducts associés au paiement
        List<PaymentProduct> paymentProducts = paymentProductRepository.findByPayment(payment);

        List<PaymentResponse.ProductDetail> productDetails = paymentProducts.stream()
                .map(paymentProduct -> PaymentResponse.ProductDetail.builder()
                        .productId(paymentProduct.getProduct().getId().toString())
                        .quantity(paymentProduct.getQuantity())
                        .unitPrice(paymentProduct.getUnitPrice())
                        .totalPrice(paymentProduct.getUnitPrice().multiply(BigDecimal.valueOf(paymentProduct.getQuantity())))
                        .build())
                .collect(Collectors.toList());

        return PaymentResponse.builder()
                .id(payment.getId())
                .customerId(payment.getCustomer().getId())
                .totalAmount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod().name())
                .status(payment.getStatus().name())
                .transactionId(payment.getTransactionId())
                .products(productDetails)
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
} 