package com.Gestion.PromiereVersion.dto;

import com.Gestion.PromiereVersion.model.Product;
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
    private String imagePath;

    public static ProductDTO fromProduct(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock() != null ? product.getStock() : 0)
                .codeBarre(product.getCodeBarre())
                .barcode(product.getBarcode())
                .barcodeImagePath(product.getBarcodeImagePath())
                .isVisible(product.getIsVisible())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .taxId(product.getTax() != null ? product.getTax().getId() : null)
                .profitMargin(product.getProfitMargin())
                .imagePath(product.getImagePath())
                .build();
    }

    public Product toProduct() {
        return Product.builder()
                .id(id)
                .name(name)
                .description(description)
                .price(price)
                .stock(stock != null ? stock : 0)
                .codeBarre(codeBarre)
                .barcode(barcode)
                .barcodeImagePath(barcodeImagePath)
                .isVisible(isVisible)
                .profitMargin(profitMargin)
                .imagePath(imagePath)
                .build();
    }
} 