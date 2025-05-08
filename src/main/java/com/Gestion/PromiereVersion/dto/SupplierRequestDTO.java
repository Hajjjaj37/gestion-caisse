package com.Gestion.PromiereVersion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierRequestDTO {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String companyName;
    private String taxNumber;
    private String bankAccount;
    private String paymentTerms;
    private List<Long> productIds;
} 