package com.Gestion.PromiereVersion.service;

import com.Gestion.PromiereVersion.dto.ProductDTO;
import com.Gestion.PromiereVersion.model.*;
import com.Gestion.PromiereVersion.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final TaxRepository taxRepository;
    private final SupplierRepository supplierRepository;
    private final BarcodeService barcodeService;
    private final String uploadDir = "uploads/products";
    private final Path rootLocation = Paths.get("uploads");

    @Transactional
    public Product createProduct(ProductDTO productDTO, MultipartFile image) {
        log.info("Début de la création du produit avec les données: {}", productDTO);

        // Validation des données obligatoires
        if (productDTO == null) {
            String error = "Les données du produit sont manquantes";
            log.error(error);
            throw new IllegalArgumentException(error);
        }

        if (productDTO.getName() == null || productDTO.getName().trim().isEmpty()) {
            String error = "Le nom du produit est obligatoire";
            log.error(error);
            throw new IllegalArgumentException(error);
        }

        if (productDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            String error = "Le prix du produit doit être supérieur à 0";
            log.error(error);
            throw new IllegalArgumentException(error);
        }

        if (productDTO.getStock() < 0) {
            String error = "Le stock du produit ne peut pas être négatif";
            log.error(error);
            throw new IllegalArgumentException(error);
        }

        if (productDTO.getCategoryId() == null) {
            String error = "L'ID de la catégorie est obligatoire";
            log.error(error);
            throw new IllegalArgumentException(error);
        }

        // Génération automatique du code-barres
        String codeBarre = barcodeService.generateBarcode();
        String barcodeImagePath = null;
        try {
            barcodeImagePath = barcodeService.generateBarcodeImage(codeBarre);
        } catch (Exception e) {
            log.error("Erreur lors de la génération de l'image du code-barres", e);
        }
        
        // Vérification de l'existence de la catégorie
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Catégorie non trouvée"));

        // Vérification de l'existence de la taxe
        Tax tax = taxRepository.findById(productDTO.getTaxId())
                .orElseThrow(() -> new IllegalArgumentException("Taxe non trouvée"));

        // Vérification de l'existence du fournisseur
        Supplier supplier = null;
        if (productDTO.getSupplierId() != null) {
            supplier = supplierRepository.findById(productDTO.getSupplierId())
                    .orElseThrow(() -> new IllegalArgumentException("Fournisseur non trouvé"));
        }

        // Création du produit
        Product product = Product.builder()
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .stock(productDTO.getStock())
                .codeBarre(codeBarre)
                .barcode(codeBarre)
                .barcodeImagePath(barcodeImagePath)
                .category(category)
                .tax(tax)
                .supplier(supplier)
                .isVisible(true)
                .build();

        // Gestion de l'image
        if (image != null && !image.isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(image.getInputStream(), filePath);
                product.setImageUrl(filePath.toString());
            } catch (Exception e) {
                throw new RuntimeException("Failed to store image", e);
            }
        }

        // Sauvegarde du produit
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + id));
    }

    @Transactional
    public Product updateProduct(Long id, ProductDTO productDTO, MultipartFile image) {
        // Vérifier si le produit existe
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + id));

        // Validation des données
        if (productDTO.getName() == null || productDTO.getName().trim().isEmpty()) {
            throw new RuntimeException("Le nom du produit est obligatoire");
        }
        if (productDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Le prix doit être supérieur à 0");
        }
        if (productDTO.getStock() < 0) {
            throw new RuntimeException("Le stock ne peut pas être négatif");
        }
        if (productDTO.getCategoryId() == null) {
            throw new RuntimeException("L'ID de la catégorie est obligatoire");
        }

        // Vérifier si le nom existe déjà (sauf pour le produit actuel)
        if (productRepository.existsByNameAndIdNot(productDTO.getName(), id)) {
            throw new RuntimeException("Un autre produit avec ce nom existe déjà");
        }

        // Récupérer la catégorie
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'ID: " + productDTO.getCategoryId()));

        // Récupérer la taxe
        Tax tax = taxRepository.findById(productDTO.getTaxId())
                .orElseThrow(() -> new RuntimeException("Taxe non trouvée avec l'ID: " + productDTO.getTaxId()));

        // Récupérer le fournisseur
        Supplier supplier = null;
        if (productDTO.getSupplierId() != null) {
            supplier = supplierRepository.findById(productDTO.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé avec l'ID: " + productDTO.getSupplierId()));
        }

        // Mettre à jour le produit
        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setStock(productDTO.getStock());
        existingProduct.setCategory(category);
        existingProduct.setTax(tax);
        existingProduct.setSupplier(supplier);
        existingProduct.setIsVisible(productDTO.getIsVisible());

        // Gestion de l'image
        if (image != null && !image.isEmpty()) {
            try {
                // Supprimer l'ancienne image si elle existe
                if (existingProduct.getImageUrl() != null) {
                    deleteImage(existingProduct.getImageUrl());
                }

                // Sauvegarder la nouvelle image
                String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(image.getInputStream(), filePath);
                existingProduct.setImageUrl(filePath.toString());
            } catch (Exception e) {
                throw new RuntimeException("Failed to store image", e);
            }
        }

        // Sauvegarder les modifications
        return productRepository.save(existingProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Produit non trouvé avec l'ID: " + id);
        }
        Product product = productRepository.findById(id).orElseThrow();
        if (product.getImageUrl() != null) {
            deleteImage(product.getImageUrl());
        }
        productRepository.deleteById(id);
    }

    private void deleteImage(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Files.deleteIfExists(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    public List<Product> getVisibleProducts() {
        return productRepository.findByIsVisibleTrue();
    }

    public Product getProductByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec le code-barres: " + barcode));
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> getProductsByTax(Long taxId) {
        return productRepository.findByTaxId(taxId);
    }

    public Product getProductByCodeBarre(String codeBarre) {
        return productRepository.findByCodeBarre(codeBarre)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec le code-barres: " + codeBarre));
    }
} 