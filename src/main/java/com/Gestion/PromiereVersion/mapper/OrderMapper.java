package com.Gestion.PromiereVersion.mapper;

import com.Gestion.PromiereVersion.dto.*;
import com.Gestion.PromiereVersion.model.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {
    public OrderResponseDTO toOrderResponseDTO(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .customer(CustomerDTO.builder()
                        .id(order.getCustomer().getId())
                        .firstName(order.getCustomer().getFirstName())
                        .lastName(order.getCustomer().getLastName())
                        .email(order.getCustomer().getEmail())
                        .phone(order.getCustomer().getPhone())
                        .address(order.getCustomer().getAddress())
                        .customerCardNumber(order.getCustomer().getCustomerCardNumber())
                        .tax(order.getCustomer().getTax())
                        .build())
                .payment(PaymentDTO.builder()
                        .id(order.getPayment().getId())
                        .amount(order.getPayment().getAmount())
                        .paymentMethod(order.getPayment().getPaymentMethod())
                        .status(order.getPayment().getStatus())
                        .transactionId(order.getPayment().getTransactionId())
                        .createdAt(order.getPayment().getCreatedAt())
                        .updatedAt(order.getPayment().getUpdatedAt())
                        .build())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .items(order.getItems().stream()
                        .map(this::toOrderItemDTO)
                        .collect(Collectors.toList()))
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private OrderItemDTO toOrderItemDTO(OrderItem orderItem) {
        return OrderItemDTO.builder()
                .id(orderItem.getId())
                .product(ProductDTO.builder()
                        .id(orderItem.getProduct().getId())
                        .name(orderItem.getProduct().getName())
                        .description(orderItem.getProduct().getDescription())
                        .price(orderItem.getProduct().getPrice())
                        .stock(orderItem.getProduct().getStock())
                        .codeBarre(orderItem.getProduct().getCodeBarre())
                        .barcode(orderItem.getProduct().getBarcode())
                        .barcodeImagePath(orderItem.getProduct().getBarcodeImagePath())
                        .categoryId(orderItem.getProduct().getCategory().getId())
                        .taxId(orderItem.getProduct().getTax().getId())
                        .isVisible(orderItem.getProduct().getIsVisible())
                        .build())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .totalPrice(orderItem.getTotalPrice())
                .build();
    }
} 