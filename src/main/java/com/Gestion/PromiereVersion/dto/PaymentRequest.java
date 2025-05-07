package com.Gestion.PromiereVersion.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

@Data
public class PaymentRequest {
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Products are required")
    private List<ProductDetail> products;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @Data
    public static class ProductDetail {
        @NotBlank(message = "Product ID is required")
        private String productId;

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than zero")
        private Integer quantity;

        @NotNull(message = "Unit price is required")
        @Positive(message = "Unit price must be greater than zero")
        private Double unitPrice;
    }
} 