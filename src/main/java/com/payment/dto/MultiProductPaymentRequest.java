package com.payment.dto;

import lombok.Data;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class MultiProductPaymentRequest {
    @NotEmpty(message = "At least one product is required")
    @Valid
    private List<ProductPaymentDetail> products;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
} 