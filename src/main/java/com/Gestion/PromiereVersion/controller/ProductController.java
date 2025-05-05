package com.Gestion.PromiereVersion.controller;

import com.Gestion.PromiereVersion.dto.ErrorResponse;
import com.Gestion.PromiereVersion.dto.ProductDTO;
import com.Gestion.PromiereVersion.model.Product;
import com.Gestion.PromiereVersion.service.BarcodeService;
import com.Gestion.PromiereVersion.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final BarcodeService barcodeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDTO> createProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("stock") Integer stock,
            @RequestParam("isVisible") Boolean isVisible,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("taxId") Long taxId,
            @RequestParam("supplierId") Long supplierId,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            ProductDTO productDTO = ProductDTO.builder()
                    .name(name)
                    .description(description)
                    .price(price)
                    .stock(stock)
                    .isVisible(isVisible)
                    .categoryId(categoryId)
                    .taxId(taxId)
                    .supplierId(supplierId)
                    .build();
            Product product = productService.createProduct(productDTO, image);
            return ResponseEntity.ok(ProductDTO.fromProduct(product));
        } catch (Exception e) {
            log.error("Erreur lors de la création du produit", e);
            return ResponseEntity.badRequest()
                    .body(ProductDTO.builder()
                            .name("Erreur: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            List<ProductDTO> productDTOs = products.stream()
                    .map(ProductDTO::fromProduct)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(productDTOs);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des produits", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/visible")
    public ResponseEntity<List<ProductDTO>> getVisibleProducts() {
        try {
            List<Product> products = productService.getVisibleProducts();
            List<ProductDTO> productDTOs = products.stream()
                    .map(ProductDTO::fromProduct)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(productDTOs);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des produits visibles", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(ProductDTO.fromProduct(product));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du produit", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ProductDTO.builder()
                            .name("Erreur: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ProductDTO> getProductByBarcode(@PathVariable String barcode) {
        try {
            Product product = productService.getProductByBarcode(barcode);
            return ResponseEntity.ok(ProductDTO.fromProduct(product));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du produit par code-barres", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ProductDTO.builder()
                            .name("Erreur: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") ProductDTO productDTO,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            Product product = productService.updateProduct(id, productDTO, image);
            return ResponseEntity.ok(ProductDTO.fromProduct(product));
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du produit", e);
            return ResponseEntity.badRequest()
                    .body(ProductDTO.builder()
                            .name("Erreur: " + e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du produit", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        try {
            List<Product> products = productService.getProductsByCategory(categoryId);
            List<ProductDTO> productDTOs = products.stream()
                    .map(ProductDTO::fromProduct)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(productDTOs);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des produits par catégorie", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/tax/{taxId}")
    public ResponseEntity<List<ProductDTO>> getProductsByTax(@PathVariable Long taxId) {
        try {
            List<Product> products = productService.getProductsByTax(taxId);
            List<ProductDTO> productDTOs = products.stream()
                    .map(ProductDTO::fromProduct)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(productDTOs);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des produits par taxe", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/code-barre/{codeBarre}")
    public ResponseEntity<ProductDTO> getProductByCodeBarre(@PathVariable String codeBarre) {
        try {
            Product product = productService.getProductByCodeBarre(codeBarre);
            return ResponseEntity.ok(ProductDTO.fromProduct(product));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du produit par code-barres", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ProductDTO.builder()
                            .name("Erreur: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/barcode/{codeBarre}/image")
    public ResponseEntity<Resource> getBarcodeImage(@PathVariable String codeBarre) {
        try {
            Resource imageResource = barcodeService.getBarcodeImage(codeBarre);
            if (imageResource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(imageResource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'image du code-barres", e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<Resource> getProductImage(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            if (product.getImageUrl() == null) {
                return ResponseEntity.notFound().build();
            }
            Path imagePath = Paths.get(product.getImageUrl());
            if (!Files.exists(imagePath)) {
                return ResponseEntity.notFound().build();
            }
            Resource imageResource = new UrlResource(imagePath.toUri());
            String contentType = Files.probeContentType(imagePath);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                    .body(imageResource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
} 