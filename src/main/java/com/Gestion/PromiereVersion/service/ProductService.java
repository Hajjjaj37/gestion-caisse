package com.Gestion.PromiereVersion.service;

import com.Gestion.PromiereVersion.dto.ProductDTO;
import com.Gestion.PromiereVersion.model.Category;
import com.Gestion.PromiereVersion.model.Product;
import com.Gestion.PromiereVersion.model.Tax;
import com.Gestion.PromiereVersion.repository.CategoryRepository;
import com.Gestion.PromiereVersion.repository.ProductRepository;
import com.Gestion.PromiereVersion.repository.TaxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final TaxRepository taxRepository;
    private final BarcodeService barcodeService;

    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
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
        productDTO.setCodeBarre(codeBarre);
        
        // Génération de l'image du code-barres
        String barcodeImagePath;
        try {
            barcodeImagePath = barcodeService.generateBarcodeImage(codeBarre);
            productDTO.setBarcodeImagePath(barcodeImagePath);
        } catch (Exception e) {
            log.error("Erreur lors de la génération de l'image du code-barres", e);
            throw new RuntimeException("Erreur lors de la génération de l'image du code-barres", e);
        }

        // Vérification de l'existence de la catégorie
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Catégorie non trouvée"));

        // Vérification de l'existence de la taxe
        Tax tax = taxRepository.findById(productDTO.getTaxId())
                .orElseThrow(() -> new IllegalArgumentException("Taxe non trouvée"));

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
                .isVisible(productDTO.getIsVisible())
                .build();

        // Sauvegarde du produit
        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getVisibleProducts() {
        return productRepository.findByIsVisibleTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsByTax(Long taxId) {
        return productRepository.findByTaxId(taxId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + id));
    }

    public ProductDTO getProductByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec le code-barres: " + barcode));
    }

    public ProductDTO getProductByCodeBarre(String codeBarre) {
        return productRepository.findByCodeBarre(codeBarre)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec le code-barres: " + codeBarre));
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
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

        // Vérifier si le nouveau code-barres est différent et existe déjà
        if (!existingProduct.getBarcode().equals(productDTO.getBarcode()) && 
            productRepository.existsByBarcode(productDTO.getBarcode())) {
            throw new RuntimeException("Un produit avec ce code-barres existe déjà");
        }

        // Récupérer la catégorie
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'ID: " + productDTO.getCategoryId()));

        // Récupérer la taxe
        Tax tax = taxRepository.findById(productDTO.getTaxId())
                .orElseThrow(() -> new RuntimeException("Taxe non trouvée avec l'ID: " + productDTO.getTaxId()));

        // Mettre à jour le produit
        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setStock(productDTO.getStock());
        existingProduct.setBarcode(productDTO.getBarcode());
        existingProduct.setCategory(category);
        existingProduct.setTax(tax);
        existingProduct.setIsVisible(productDTO.getIsVisible());

        // Sauvegarder les modifications
        Product updatedProduct = productRepository.save(existingProduct);

        // Retourner le DTO
        return convertToDTO(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Produit non trouvé avec l'ID: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductDTO convertToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .barcode(product.getBarcode())
                .barcodeImagePath(product.getBarcodeImagePath())
                .categoryId(product.getCategory().getId())
                .taxId(product.getTax().getId())
                .isVisible(product.getIsVisible())
                .build();
    }

    @Transactional
    public Product createProduct(Product product) {
        // Générer le code-barres
        String barcode = barcodeService.generateBarcode();
        product.setBarcode(barcode);

        // Générer l'image du code-barres
        String barcodeImagePath;
        try {
            barcodeImagePath = barcodeService.generateBarcodeImage(barcode);
            product.setBarcodeImagePath(barcodeImagePath);
        } catch (Exception e) {
            log.error("Erreur lors de la génération de l'image du code-barres", e);
            throw new RuntimeException("Erreur lors de la génération de l'image du code-barres", e);
        }

        // Sauvegarder le produit
        Product savedProduct = productRepository.save(product);
        log.info("Produit créé avec succès: {}", savedProduct);

        return savedProduct;
    }
} 