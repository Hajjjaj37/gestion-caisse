package com.Gestion.PromiereVersion.dto;

import com.Gestion.PromiereVersion.model.Supplier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDTO {
    private Long id;
    private String name;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String country;
    private List<Long> productIds;
    private List<ProductDTO> products;

    public static SupplierDTO fromSupplier(Supplier supplier) {
        return SupplierDTO.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .contactPerson(supplier.getContactPerson())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .address(supplier.getAddress())
                .city(supplier.getCity())
                .country(supplier.getCountry())
                .productIds(supplier.getProducts() != null ? 
                    supplier.getProducts().stream()
                        .map(product -> product.getId())
                        .collect(Collectors.toList()) : null)
                .products(supplier.getProducts() != null ? 
                    supplier.getProducts().stream()
                        .map(product -> ProductDTO.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .description(product.getDescription())
                            .price(product.getPrice())
                            .build())
                        .collect(Collectors.toList()) : null)
                .build();
    }

    public Supplier toSupplier() {
        return Supplier.builder()
                .id(this.id)
                .name(this.name)
                .contactPerson(this.contactPerson)
                .email(this.email)
                .phone(this.phone)
                .address(this.address)
                .city(this.city)
                .country(this.country)
                .build();
    }
} 