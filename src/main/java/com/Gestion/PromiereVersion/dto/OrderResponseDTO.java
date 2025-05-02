package com.Gestion.PromiereVersion.dto;

import com.Gestion.PromiereVersion.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private Long id;
    private String orderNumber;
    private CustomerDTO customer;
    private BigDecimal totalAmount;
    private String status;
    private String paymentStatus;
    private String paymentMethod;
    private PaymentDTO payment;
    private String notes;
    private List<OrderItemDTO> orderItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerDTO {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String address;
        private String customerCardNumber;
        private TaxDTO tax;

        public static CustomerDTO fromCustomer(Customer customer) {
            if (customer == null) return null;
            return CustomerDTO.builder()
                    .id(customer.getId())
                    .name(customer.getName())
                    .email(customer.getEmail())
                    .phone(customer.getPhone())
                    .address(customer.getAddress())
                    .customerCardNumber(customer.getCustomerCardNumber())
                    .tax(customer.getTax() != null ? TaxDTO.fromTax(customer.getTax()) : null)
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaxDTO {
        private Long id;
        private String name;
        private Double rate;
        private String description;

        public static TaxDTO fromTax(Tax tax) {
            if (tax == null) return null;
            return TaxDTO.builder()
                    .id(tax.getId())
                    .name(tax.getName())
                    .rate(tax.getRate())
                    .description(tax.getDescription())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentDTO {
        private Long id;
        private BigDecimal amount;
        private String paymentMethod;
        private String transactionId;
        private String status;
        private LocalDateTime paymentDate;
        private String notes;

        public static PaymentDTO fromPayment(Payment payment) {
            if (payment == null) return null;
            return PaymentDTO.builder()
                    .id(payment.getId())
                    .amount(payment.getAmount())
                    .paymentMethod(payment.getPaymentMethod())
                    .transactionId(payment.getTransactionId())
                    .status(payment.getStatus())
                    .paymentDate(payment.getPaymentDate())
                    .notes(payment.getNotes())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private Long id;
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;

        public static OrderItemDTO fromOrderItem(OrderItem orderItem) {
            if (orderItem == null) return null;
            return OrderItemDTO.builder()
                    .id(orderItem.getId())
                    .productId(orderItem.getProduct().getId())
                    .productName(orderItem.getProduct().getName())
                    .quantity(orderItem.getQuantity())
                    .unitPrice(orderItem.getUnitPrice())
                    .totalPrice(orderItem.getTotalPrice())
                    .build();
        }
    }

    public static OrderResponseDTO fromOrder(Order order) {
        if (order == null) return null;
        return OrderResponseDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customer(CustomerDTO.fromCustomer(order.getCustomer()))
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(order.getPaymentMethod())
                .payment(PaymentDTO.fromPayment(order.getPayment()))
                .notes(order.getNotes())
                .orderItems(order.getOrderItems() != null ? 
                    order.getOrderItems().stream()
                        .map(OrderItemDTO::fromOrderItem)
                        .collect(Collectors.toList()) : null)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
} 