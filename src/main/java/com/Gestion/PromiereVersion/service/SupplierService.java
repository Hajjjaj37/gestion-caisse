package com.Gestion.PromiereVersion.service;

import com.Gestion.PromiereVersion.dto.SupplierDTO;
import com.Gestion.PromiereVersion.model.Product;
import com.Gestion.PromiereVersion.model.Supplier;
import com.Gestion.PromiereVersion.repository.ProductRepository;
import com.Gestion.PromiereVersion.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    @Transactional
    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        log.info("Creating supplier: {}", supplierDTO.getName());
        
        if (supplierRepository.existsByEmail(supplierDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Supplier supplier = supplierDTO.toSupplier();
        
        // Associer les produits si des IDs sont fournis
        if (supplierDTO.getProductIds() != null && !supplierDTO.getProductIds().isEmpty()) {
            List<Product> products = productRepository.findAllById(supplierDTO.getProductIds());
            if (products.size() != supplierDTO.getProductIds().size()) {
                throw new IllegalArgumentException("One or more products not found");
            }
            // Associer les produits au fournisseur
            products.forEach(product -> {
                product.setSupplier(supplier);
                productRepository.save(product);
            });
            supplier.setProducts(products);
        }

        Supplier savedSupplier = supplierRepository.save(supplier);
        return SupplierDTO.fromSupplier(savedSupplier);
    }

    @Transactional(readOnly = true)
    public List<SupplierDTO> getAllSuppliers() {
        log.info("Fetching all suppliers");
        return supplierRepository.findAll().stream()
                .map(SupplierDTO::fromSupplier)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SupplierDTO getSupplierById(Long id) {
        log.info("Fetching supplier with id: {}", id);
        return supplierRepository.findById(id)
                .map(SupplierDTO::fromSupplier)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
    }

    @Transactional
    public SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO) {
        log.info("Updating supplier with id: {}", id);
        
        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));

        if (!existingSupplier.getEmail().equals(supplierDTO.getEmail()) &&
            supplierRepository.existsByEmail(supplierDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        existingSupplier.setName(supplierDTO.getName());
        existingSupplier.setContactPerson(supplierDTO.getContactPerson());
        existingSupplier.setEmail(supplierDTO.getEmail());
        existingSupplier.setPhone(supplierDTO.getPhone());
        existingSupplier.setAddress(supplierDTO.getAddress());
        existingSupplier.setCity(supplierDTO.getCity());
        existingSupplier.setCountry(supplierDTO.getCountry());

        // Mettre à jour les produits associés
        if (supplierDTO.getProductIds() != null) {
            List<Product> products = productRepository.findAllById(supplierDTO.getProductIds());
            if (products.size() != supplierDTO.getProductIds().size()) {
                throw new IllegalArgumentException("One or more products not found");
            }
            
            // Désassocier les anciens produits
            existingSupplier.getProducts().forEach(product -> {
                product.setSupplier(null);
                productRepository.save(product);
            });
            
            // Associer les nouveaux produits
            products.forEach(product -> {
                product.setSupplier(existingSupplier);
                productRepository.save(product);
            });
            
            existingSupplier.setProducts(products);
        }

        Supplier updatedSupplier = supplierRepository.save(existingSupplier);
        return SupplierDTO.fromSupplier(updatedSupplier);
    }

    @Transactional
    public void deleteSupplier(Long id) {
        log.info("Deleting supplier with id: {}", id);
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
                
        // Désassocier tous les produits avant de supprimer le fournisseur
        supplier.getProducts().forEach(product -> {
            product.setSupplier(null);
            productRepository.save(product);
        });
        
        supplierRepository.delete(supplier);
    }
} 