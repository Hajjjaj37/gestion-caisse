package com.Gestion.PromiereVersion.dto;

import com.Gestion.PromiereVersion.model.Tax;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxDTO {
    private Long id;
    private String name;
    private Double rate;
    private String description;

    public static TaxDTO fromTax(Tax tax) {
        return TaxDTO.builder()
                .id(tax.getId())
                .name(tax.getName())
                .rate(tax.getRate())
                .description(tax.getDescription())
                .build();
    }
} 