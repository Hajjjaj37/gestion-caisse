package com.Gestion.PromiereVersion.dto;

import com.Gestion.PromiereVersion.model.Category;
import com.Gestion.PromiereVersion.model.Tax;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private String imageUrl;
    private Long categoryId;
    private Long taxId;
    private Long supplierId;
    private Boolean isVisible;
    private BigDecimal profitMargin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductDTO fromProduct(com.Gestion.PromiereVersion.model.Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setCodeBarre(product.getCodeBarre());
        dto.setBarcode(product.getBarcode());
        dto.setBarcodeImagePath(product.getBarcodeImagePath());
        dto.setImageUrl(product.getImageUrl());
        dto.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
        dto.setTaxId(product.getTax() != null ? product.getTax().getId() : null);
        dto.setSupplierId(product.getSupplier() != null ? product.getSupplier().getId() : null);
        dto.setIsVisible(product.getIsVisible());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }
} 