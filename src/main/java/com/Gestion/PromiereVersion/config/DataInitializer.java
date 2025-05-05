package com.Gestion.PromiereVersion.config;

import com.Gestion.PromiereVersion.model.Category;
import com.Gestion.PromiereVersion.model.Tax;
import com.Gestion.PromiereVersion.repository.CategoryRepository;
import com.Gestion.PromiereVersion.repository.TaxRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final CategoryRepository categoryRepository;
    private final TaxRepository taxRepository;

    @PostConstruct
    public void init() {
        // Initialize categories if none exist
        if (categoryRepository.count() == 0) {
            Category electronics = Category.builder()
                    .name("Électronique")
                    .description("Produits électroniques et accessoires")
                    .build();

            Category clothing = Category.builder()
                    .name("Vêtements")
                    .description("Vêtements pour hommes, femmes et enfants")
                    .build();

            Category food = Category.builder()
                    .name("Alimentation")
                    .description("Produits alimentaires et boissons")
                    .build();

            categoryRepository.save(electronics);
            categoryRepository.save(clothing);
            categoryRepository.save(food);
        }

        // Initialize taxes if none exist
        if (taxRepository.count() == 0) {
            Tax tva = Tax.builder()
                    .name("TVA")
                    .rate(new BigDecimal("20.00"))
                    .description("Taxe sur la valeur ajoutée")
                    .build();

            Tax ecoTax = Tax.builder()
                    .name("Éco-taxe")
                    .rate(new BigDecimal("5.00"))
                    .description("Taxe écologique")
                    .build();

            taxRepository.save(tva);
            taxRepository.save(ecoTax);
        }
    }
} 