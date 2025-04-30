package com.Gestion.PromiereVersion.dto;

import com.Gestion.PromiereVersion.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleProductDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private String barcode;

    public static SimpleProductDTO fromProduct(Product product) {
        return SimpleProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .barcode(product.getBarcode())
                .build();
    }
} 