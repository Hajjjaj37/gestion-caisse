package com.payment.dto;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DetailedPaymentResponse {
    private Long id;
    private List<ProductPaymentDetail> products;
    private BigDecimal totalAmount;
    private String currency;
    private String paymentMethod;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 