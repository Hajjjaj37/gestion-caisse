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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final TaxRepository taxRepository;
    private final BarcodeService barcodeService;
    private static final String UPLOAD_DIR = "uploads/products/";

    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO, MultipartFile image) {
        log.info("Début de la création du produit: {}", productDTO.getName());
        try {
            // Validation des données
            log.info("Validation des données du produit");
            if (productDTO.getName() == null || productDTO.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Le nom du produit est obligatoire");
            }
            if (productDTO.getDescription() == null || productDTO.getDescription().trim().isEmpty()) {
                throw new IllegalArgumentException("La description du produit est obligatoire");
            }
            if (productDTO.getPrice() == null || productDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Le prix du produit doit être positif");
            }
            if (productDTO.getStock() == null || productDTO.getStock() < 0) {
                throw new IllegalArgumentException("Le stock du produit doit être positif");
            }
            if (productDTO.getCategoryId() == null) {
                throw new IllegalArgumentException("L'ID de la catégorie est obligatoire");
            }
            if (productDTO.getTaxId() == null) {
                throw new IllegalArgumentException("L'ID de la taxe est obligatoire");
            }

            // Récupérer la catégorie
            log.info("Récupération de la catégorie avec l'ID: {}", productDTO.getCategoryId());
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Catégorie non trouvée avec l'ID: " + productDTO.getCategoryId()));

            // Récupérer la taxe
            log.info("Récupération de la taxe avec l'ID: {}", productDTO.getTaxId());
            Tax tax = taxRepository.findById(productDTO.getTaxId())
                    .orElseThrow(() -> new IllegalArgumentException("Taxe non trouvée avec l'ID: " + productDTO.getTaxId()));

            // Générer un code-barres si non fourni
            if (productDTO.getCodeBarre() == null || productDTO.getCodeBarre().isEmpty()) {
                log.info("Génération d'un nouveau code-barres");
                String generatedBarcode = barcodeService.generateBarcode();
                productDTO.setCodeBarre(generatedBarcode);
                productDTO.setBarcode(generatedBarcode);
            }

            // Créer le produit
            log.info("Création de l'entité Product");
            Product product = productDTO.toProduct();
            product.setCategory(category);
            product.setTax(tax);

            // Gérer l'image si fournie
            if (image != null && !image.isEmpty()) {
                log.info("Traitement de l'image du produit");
                try {
                    String imagePath = saveImage(image);
                    product.setImagePath(imagePath);
                } catch (IOException e) {
                    log.error("Erreur lors de la sauvegarde de l'image", e);
                    throw new RuntimeException("Erreur lors de la sauvegarde de l'image: " + e.getMessage());
                }
            }

            // Sauvegarder le produit
            log.info("Sauvegarde du produit dans la base de données");
            Product savedProduct = productRepository.save(product);

            // Générer l'image du code-barres
            try {
                log.info("Génération de l'image du code-barres");
                String barcodeImagePath = barcodeService.generateBarcodeImage(product.getCodeBarre());
                savedProduct.setBarcodeImagePath(barcodeImagePath);
                productRepository.save(savedProduct);
            } catch (Exception e) {
                log.error("Erreur lors de la génération de l'image du code-barres", e);
            }

            log.info("Produit créé avec succès: {}", savedProduct.getId());
            return ProductDTO.fromProduct(savedProduct);
        } catch (IllegalArgumentException e) {
            log.error("Erreur de validation: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la création du produit", e);
            throw new RuntimeException("Erreur lors de la création du produit: " + e.getMessage());
        }
    }

    private String saveImage(MultipartFile image) throws IOException {
        log.info("Début de la sauvegarde de l'image");
        // Créer le répertoire s'il n'existe pas
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            log.info("Création du répertoire de sauvegarde: {}", UPLOAD_DIR);
            Files.createDirectories(uploadPath);
        }

        // Générer un nom de fichier unique
        String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        log.info("Sauvegarde du fichier: {}", filePath);

        // Sauvegarder le fichier
        Files.copy(image.getInputStream(), filePath);
        log.info("Image sauvegardée avec succès");

        return UPLOAD_DIR + fileName;
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll().stream()
                .map(ProductDTO::fromProduct)
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

    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        log.info("Fetching product with id: {}", id);
        return productRepository.findById(id)
                .map(ProductDTO::fromProduct)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
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
    public ProductDTO updateProduct(Long id, ProductDTO productDTO, MultipartFile image) {
        log.info("Updating product with id: {}", id);
        
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!existingProduct.getCodeBarre().equals(productDTO.getCodeBarre()) &&
            productRepository.existsByCodeBarre(productDTO.getCodeBarre())) {
            throw new IllegalArgumentException("Product with this barcode already exists");
        }

        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setStock(productDTO.getStock());
        existingProduct.setCodeBarre(productDTO.getCodeBarre());
        existingProduct.setBarcode(productDTO.getBarcode());
        existingProduct.setBarcodeImagePath(productDTO.getBarcodeImagePath());
        existingProduct.setIsVisible(productDTO.getIsVisible());

        if (image != null && !image.isEmpty()) {
            try {
                String imagePath = saveImage(image);
                existingProduct.setImagePath(imagePath);
            } catch (IOException e) {
                log.error("Erreur lors de la sauvegarde de l'image", e);
                throw new RuntimeException("Erreur lors de la sauvegarde de l'image", e);
            }
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return ProductDTO.fromProduct(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found");
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