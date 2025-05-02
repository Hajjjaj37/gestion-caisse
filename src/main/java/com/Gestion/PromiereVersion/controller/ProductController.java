package com.Gestion.PromiereVersion.controller;

import com.Gestion.PromiereVersion.dto.ErrorResponse;
import com.Gestion.PromiereVersion.dto.ProductDTO;
import com.Gestion.PromiereVersion.model.Product;
import com.Gestion.PromiereVersion.service.BarcodeService;
import com.Gestion.PromiereVersion.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final BarcodeService barcodeService;
    private static final String UPLOAD_DIR = "uploads/products/";

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("stock") Integer stock,
            @RequestParam(value = "codeBarre", required = false) String codeBarre,
            @RequestParam(value = "barcode", required = false) String barcode,
            @RequestParam(value = "barcodeImagePath", required = false) String barcodeImagePath,
            @RequestParam("isVisible") Boolean isVisible,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("taxId") Long taxId,
            @RequestParam(value = "profitMargin", required = false) BigDecimal profitMargin,
            @RequestParam(value = "taxIds", required = false) List<Long> taxIds,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        try {
            // Validation des champs obligatoires
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ErrorResponse.builder()
                                .message("Le nom est obligatoire")
                                .error("Bad Request")
                                .status(HttpStatus.BAD_REQUEST.value())
                                .build());
            }

            if (description == null || description.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ErrorResponse.builder()
                                .message("La description est obligatoire")
                                .error("Bad Request")
                                .status(HttpStatus.BAD_REQUEST.value())
                                .build());
            }

            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest()
                        .body(ErrorResponse.builder()
                                .message("Le prix est obligatoire et doit être positif")
                                .error("Bad Request")
                                .status(HttpStatus.BAD_REQUEST.value())
                                .build());
            }

            if (stock == null || stock < 0) {
                return ResponseEntity.badRequest()
                        .body(ErrorResponse.builder()
                                .message("Le stock est obligatoire et doit être positif")
                                .error("Bad Request")
                                .status(HttpStatus.BAD_REQUEST.value())
                                .build());
            }

            if (isVisible == null) {
                return ResponseEntity.badRequest()
                        .body(ErrorResponse.builder()
                                .message("La visibilité est obligatoire")
                                .error("Bad Request")
                                .status(HttpStatus.BAD_REQUEST.value())
                                .build());
            }

            if (categoryId == null) {
                return ResponseEntity.badRequest()
                        .body(ErrorResponse.builder()
                                .message("L'ID de la catégorie est obligatoire")
                                .error("Bad Request")
                                .status(HttpStatus.BAD_REQUEST.value())
                                .build());
            }

            if (taxId == null) {
                return ResponseEntity.badRequest()
                        .body(ErrorResponse.builder()
                                .message("L'ID de la taxe est obligatoire")
                                .error("Bad Request")
                                .status(HttpStatus.BAD_REQUEST.value())
                                .build());
            }

            ProductDTO productDTO = ProductDTO.builder()
                    .name(name)
                    .description(description)
                    .price(price)
                    .stock(stock)
                    .codeBarre(codeBarre)
                    .barcode(barcode)
                    .barcodeImagePath(barcodeImagePath)
                    .isVisible(isVisible)
                    .categoryId(categoryId)
                    .taxId(taxId)
                    .profitMargin(profitMargin)
                    .taxIds(taxIds)
                    .build();

            return ResponseEntity.ok(productService.createProduct(productDTO, image));
        } catch (Exception e) {
            log.error("Erreur lors de la création du produit", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.builder()
                            .message(e.getMessage())
                            .error("Internal Server Error")
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/visible")
    public ResponseEntity<List<ProductDTO>> getVisibleProducts() {
        return ResponseEntity.ok(productService.getVisibleProducts());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<?> getProductByBarcode(@PathVariable String barcode) {
        try {
            return ResponseEntity.ok(productService.getProductByBarcode(barcode));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder()
                            .message(e.getMessage())
                            .error("Not Found")
                            .status(HttpStatus.NOT_FOUND.value())
                            .build());
        }
    }

    @GetMapping("/code-barre/{codeBarre}")
    public ResponseEntity<?> getProductByCodeBarre(@PathVariable String codeBarre) {
        try {
            return ResponseEntity.ok(productService.getProductByCodeBarre(codeBarre));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder()
                            .message(e.getMessage())
                            .error("Not Found")
                            .status(HttpStatus.NOT_FOUND.value())
                            .build());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") ProductDTO productDTO,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(productService.updateProduct(id, productDTO, image));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    @GetMapping("/tax/{taxId}")
    public ResponseEntity<List<ProductDTO>> getProductsByTax(@PathVariable Long taxId) {
        return ResponseEntity.ok(productService.getProductsByTax(taxId));
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
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/image")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Resource> getProductImage(@PathVariable Long id) {
        log.info("Fetching image for product with id: {}", id);
        try {
            Product product = productService.getProductById(id).toProduct();
            if (product.getImagePath() == null || product.getImagePath().isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Path imagePath = Paths.get(product.getImagePath());
            if (!Files.exists(imagePath)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new org.springframework.core.io.FileSystemResource(imagePath);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } catch (Exception e) {
            log.error("Error fetching product image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/barcode-image")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Resource> getBarcodeImage(@PathVariable Long id) {
        log.info("Fetching barcode image for product with id: {}", id);
        try {
            Product product = productService.getProductById(id).toProduct();
            if (product.getBarcodeImagePath() == null || product.getBarcodeImagePath().isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Path imagePath = Paths.get(product.getBarcodeImagePath());
            if (!Files.exists(imagePath)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new org.springframework.core.io.FileSystemResource(imagePath);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(resource);
        } catch (Exception e) {
            log.error("Error fetching barcode image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 