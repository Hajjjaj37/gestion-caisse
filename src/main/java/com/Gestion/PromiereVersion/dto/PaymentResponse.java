package com.Gestion.PromiereVersion.dto;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PaymentResponse {
    private Long id;
    private Long customerId;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String status;
    private String transactionId;
    private List<ProductDetail> products;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    public static class ProductDetail {
        private String productId;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }
} 