package com.Gestion.PromiereVersion.controller;

import com.Gestion.PromiereVersion.dto.TaxDTO;
import com.Gestion.PromiereVersion.model.Tax;
import com.Gestion.PromiereVersion.service.TaxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/taxes")
@RequiredArgsConstructor
public class TaxController {

    private final TaxService taxService;

    @PostMapping
    public ResponseEntity<TaxDTO> createTax(@RequestBody TaxDTO taxDTO) {
        Tax tax = taxService.createTax(taxDTO);
        return ResponseEntity.ok(TaxDTO.fromTax(tax));
    }

    @GetMapping
    public ResponseEntity<List<TaxDTO>> getAllTaxes() {
        List<Tax> taxes = taxService.getAllTaxes();
        List<TaxDTO> taxDTOs = taxes.stream()
                .map(TaxDTO::fromTax)
                .collect(Collectors.toList());
        return ResponseEntity.ok(taxDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaxDTO> getTaxById(@PathVariable Long id) {
        Tax tax = taxService.getTaxById(id);
        return ResponseEntity.ok(TaxDTO.fromTax(tax));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaxDTO> updateTax(@PathVariable Long id, @RequestBody TaxDTO taxDTO) {
        Tax tax = taxService.updateTax(id, taxDTO);
        return ResponseEntity.ok(TaxDTO.fromTax(tax));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTax(@PathVariable Long id) {
        taxService.deleteTax(id);
        return ResponseEntity.ok().build();
    }
} 