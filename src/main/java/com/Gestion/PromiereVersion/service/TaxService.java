package com.Gestion.PromiereVersion.service;

import com.Gestion.PromiereVersion.dto.TaxDTO;
import com.Gestion.PromiereVersion.model.Tax;
import com.Gestion.PromiereVersion.repository.TaxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaxService {
    private final TaxRepository taxRepository;

    public Tax createTax(TaxDTO taxDTO) {
        Tax tax = Tax.builder()
                .name(taxDTO.getName())
                .rate(taxDTO.getRate())
                .description(taxDTO.getDescription())
                .build();
        return taxRepository.save(tax);
    }

    public List<Tax> getAllTaxes() {
        return taxRepository.findAll();
    }

    public Tax getTaxById(Long id) {
        return taxRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Taxe non trouvée avec l'ID: " + id));
    }

    public Tax updateTax(Long id, TaxDTO taxDTO) {
        Tax tax = getTaxById(id);
        tax.setName(taxDTO.getName());
        tax.setRate(taxDTO.getRate());
        tax.setDescription(taxDTO.getDescription());
        return taxRepository.save(tax);
    }

    public void deleteTax(Long id) {
        taxRepository.deleteById(id);
    }
} 