package com.Gestion.PromiereVersion.service;

import com.Gestion.PromiereVersion.dto.ProductDTO;
import com.Gestion.PromiereVersion.dto.SupplierRequestDTO;
import com.Gestion.PromiereVersion.dto.SupplierResponseDTO;
import com.Gestion.PromiereVersion.model.Supplier;
import com.Gestion.PromiereVersion.model.Product;
import com.Gestion.PromiereVersion.repository.SupplierRepository;
import com.Gestion.PromiereVersion.repository.ProductRepository;
import com.Gestion.PromiereVersion.repository.OrderItemRepository;
import com.Gestion.PromiereVersion.repository.PaymentProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentProductRepository paymentProductRepository;

    @Transactional
    public SupplierResponseDTO createSupplier(SupplierRequestDTO request) {
        // Vérifier si l'email existe déjà
        if (supplierRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Un fournisseur avec cet email existe déjà");
        }

        // Vérifier si le numéro de taxe existe déjà
        if (supplierRepository.existsByTaxNumber(request.getTaxNumber())) {
            throw new RuntimeException("Un fournisseur avec ce numéro de taxe existe déjà");
        }

        // Créer le fournisseur avec une liste de produits initialisée
        Supplier supplier = Supplier.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .companyName(request.getCompanyName())
                .taxNumber(request.getTaxNumber())
                .bankAccount(request.getBankAccount())
                .paymentTerms(request.getPaymentTerms())
                .products(new ArrayList<>())
                .build();

        // Sauvegarder le fournisseur
        Supplier savedSupplier = supplierRepository.save(supplier);

        // Associer les produits au fournisseur
        if (request.getProductIds() != null && !request.getProductIds().isEmpty()) {
            List<Product> products = productRepository.findAllById(request.getProductIds());
            
            // Vérifier si tous les produits existent
            if (products.size() != request.getProductIds().size()) {
                throw new RuntimeException("Certains produits n'ont pas été trouvés");
            }

            // Établir la relation bidirectionnelle
            for (Product product : products) {
                product.setSupplier(savedSupplier);
                savedSupplier.getProducts().add(product);
            }
            
            // Sauvegarder les produits mis à jour
            productRepository.saveAll(products);
            
            // Sauvegarder le fournisseur mis à jour
            savedSupplier = supplierRepository.save(savedSupplier);
        }

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

    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        // Récupérer tous les produits du fournisseur
        List<Product> products = supplier.getProducts();

        // Pour chaque produit
        for (Product product : products) {
            // Supprimer les éléments de commande associés au produit
            orderItemRepository.deleteByProductId(product.getId());
            
            // Supprimer les enregistrements de payment_products
            paymentProductRepository.deleteByProductId(product.getId());
            
            // Supprimer le produit
            productRepository.delete(product);
        }

        // Supprimer le fournisseur
        supplierRepository.delete(supplier);
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