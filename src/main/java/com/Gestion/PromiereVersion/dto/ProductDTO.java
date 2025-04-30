package com.Gestion.PromiereVersion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String codeBarre;
    private String barcode;
    private String barcodeImagePath;
    private Long categoryId;
    private Long taxId;
    private Boolean isVisible;
    private BigDecimal profitMargin;
    private List<Long> taxIds;
    private String barcodeImage;
    private Set<Long> taxIdsSet;
} 