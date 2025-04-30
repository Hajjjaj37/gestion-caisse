package com.Gestion.PromiereVersion.service;

import com.Gestion.PromiereVersion.model.Product;
import com.Gestion.PromiereVersion.repository.ProductRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.io.FileNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class BarcodeService {

    private final ProductRepository productRepository;
    private static final String BARCODE_IMAGES_DIR = "barcode-images";
    private static final int WIDTH = 300;
    private static final int HEIGHT = 300;

    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(BARCODE_IMAGES_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Dossier des codes-barres créé: {}", path.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Erreur lors de la création du répertoire des codes-barres", e);
        }
    }

    public String generateBarcode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public String generateBarcodeImage(String barcode) throws Exception {
        try {
            // Créer le nom du fichier
            String fileName = barcode + ".png";
            Path filePath = Paths.get(BARCODE_IMAGES_DIR, fileName);
            
            // Vérifier si l'image existe déjà
            if (Files.exists(filePath)) {
                log.info("Image du code-barres existe déjà: {}", filePath);
                return filePath.toString();
            }

            // Générer le QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(barcode, BarcodeFormat.QR_CODE, WIDTH, HEIGHT);
            
            // Écrire l'image
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", filePath);
            log.info("Image du code-barres générée: {}", filePath);
            
            return filePath.toString();
        } catch (Exception e) {
            log.error("Erreur lors de la génération de l'image du code-barres", e);
            throw e;
        }
    }

    public Resource getBarcodeImage(String codeBarre) throws IOException {
        String fileName = codeBarre + ".png";
        Path filePath = Paths.get(BARCODE_IMAGES_DIR, fileName);
        
        if (!Files.exists(filePath)) {
            log.error("Image du code-barres non trouvée: {}", filePath);
            throw new FileNotFoundException("Image du code-barres non trouvée: " + fileName);
        }
        
        log.info("Image du code-barres trouvée: {}", filePath);
        return new FileSystemResource(filePath.toFile());
    }

    public Resource getProductBarcodeImage(Long productId) throws IOException {
        // Récupérer le produit
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IOException("Produit non trouvé avec l'ID: " + productId));

        // Vérifier si le produit a un code-barres
        if (product.getCodeBarre() == null || product.getCodeBarre().trim().isEmpty()) {
            throw new IOException("Le produit n'a pas de code-barres");
        }

        // Récupérer l'image du code-barres
        return getBarcodeImage(product.getCodeBarre());
    }
} 