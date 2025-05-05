package com.Gestion.PromiereVersion.dto;

import com.Gestion.PromiereVersion.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    private Long customerId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String cardNumber;  // For card payments
    private String cardHolderName;
    private String expiryDate;
    private String cvv;
    private List<PaymentProductDTO> products;
} 