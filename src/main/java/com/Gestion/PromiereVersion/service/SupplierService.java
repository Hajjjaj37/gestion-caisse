package com.Gestion.PromiereVersion.service;

import com.Gestion.PromiereVersion.dto.ProductDTO;
import com.Gestion.PromiereVersion.dto.SupplierRequestDTO;
import com.Gestion.PromiereVersion.dto.SupplierResponseDTO;
import com.Gestion.PromiereVersion.model.Supplier;
import com.Gestion.PromiereVersion.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final ProductService productService;

    public SupplierResponseDTO createSupplier(SupplierRequestDTO request) {
        Supplier supplier = Supplier.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .companyName(request.getCompanyName())
                .taxNumber(request.getTaxNumber())
                .bankAccount(request.getBankAccount())
                .paymentTerms(request.getPaymentTerms())
                .build();

        Supplier savedSupplier = supplierRepository.save(supplier);
        return convertToDTO(savedSupplier);
    }

    public List<SupplierResponseDTO> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public SupplierResponseDTO getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
    }

    public List<ProductDTO> getSupplierProducts(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
        
        return supplier.getProducts().stream()
                .map(ProductDTO::fromProduct)
                .collect(Collectors.toList());
    }

    public SupplierResponseDTO updateSupplier(Long id, SupplierRequestDTO request) {
        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        existingSupplier.setName(request.getName());
        existingSupplier.setEmail(request.getEmail());
        existingSupplier.setPhone(request.getPhone());
        existingSupplier.setAddress(request.getAddress());
        existingSupplier.setCompanyName(request.getCompanyName());
        existingSupplier.setTaxNumber(request.getTaxNumber());
        existingSupplier.setBankAccount(request.getBankAccount());
        existingSupplier.setPaymentTerms(request.getPaymentTerms());

        Supplier updatedSupplier = supplierRepository.save(existingSupplier);
        return convertToDTO(updatedSupplier);
    }

    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Supplier not found with id: " + id);
        }
        supplierRepository.deleteById(id);
    }

    private SupplierResponseDTO convertToDTO(Supplier supplier) {
        return SupplierResponseDTO.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .address(supplier.getAddress())
                .companyName(supplier.getCompanyName())
                .taxNumber(supplier.getTaxNumber())
                .bankAccount(supplier.getBankAccount())
                .paymentTerms(supplier.getPaymentTerms())
                .products(supplier.getProducts().stream()
                        .map(ProductDTO::fromProduct)
                        .collect(Collectors.toList()))
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .build();
    }
} 