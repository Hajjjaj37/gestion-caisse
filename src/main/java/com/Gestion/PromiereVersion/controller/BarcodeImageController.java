package com.Gestion.PromiereVersion.controller;

import com.Gestion.PromiereVersion.dto.ErrorResponse;
import com.Gestion.PromiereVersion.service.BarcodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/barcode-images")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Permettre l'accès depuis n'importe quelle origine
public class BarcodeImageController {

    private final BarcodeService barcodeService;

    @GetMapping("/{codeBarre}")
    public ResponseEntity<?> getBarcodeImage(@PathVariable String codeBarre) {
        log.info("Tentative de récupération de l'image du code-barres: {}", codeBarre);
        
        try {
            Resource imageResource = barcodeService.getBarcodeImage(codeBarre);
            log.info("Image du code-barres trouvée: {}", codeBarre);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header("Content-Disposition", "inline; filename=\"" + codeBarre + ".png\"")
                    .body(imageResource);
        } catch (IOException e) {
            log.error("Erreur lors de la récupération de l'image du code-barres {}: {}", codeBarre, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder()
                            .message("Image du code-barres non trouvée: " + e.getMessage())
                            .error("Not Found")
                            .status(HttpStatus.NOT_FOUND.value())
                            .build());
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getProductBarcodeImage(@PathVariable Long productId) {
        log.info("Tentative de récupération de l'image du code-barres pour le produit: {}", productId);
        
        try {
            Resource imageResource = barcodeService.getProductBarcodeImage(productId);
            log.info("Image du code-barres trouvée pour le produit: {}", productId);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header("Content-Disposition", "inline; filename=\"product-" + productId + "-barcode.png\"")
                    .body(imageResource);
        } catch (IOException e) {
            log.error("Erreur lors de la récupération de l'image du code-barres pour le produit {}: {}", productId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder()
                            .message("Image du code-barres non trouvée pour le produit: " + e.getMessage())
                            .error("Not Found")
                            .status(HttpStatus.NOT_FOUND.value())
                            .build());
        }
    }
} 