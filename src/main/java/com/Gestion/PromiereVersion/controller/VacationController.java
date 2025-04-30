package com.Gestion.PromiereVersion.controller;

import com.Gestion.PromiereVersion.dto.ErrorResponse;
import com.Gestion.PromiereVersion.dto.VacationDTO;
import com.Gestion.PromiereVersion.model.VacationStatus;
import com.Gestion.PromiereVersion.service.VacationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/vacations")
@RequiredArgsConstructor
public class VacationController {

    private final VacationService vacationService;

    @PostMapping
    public ResponseEntity<VacationDTO> createVacation(@RequestBody VacationDTO vacationDTO) {
        return ResponseEntity.ok(vacationService.createVacation(vacationDTO));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateVacationStatus(
            @PathVariable Long id,
            @RequestParam VacationStatus status,
            @RequestParam(required = false) String comment) {
        try {
            log.info("Tentative de mise à jour du statut de la demande de vacances {}: {}", id, status);
            VacationDTO updatedVacation = vacationService.updateVacationStatus(id, status, comment);
            return ResponseEntity.ok(updatedVacation);
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de la mise à jour du statut: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ErrorResponse.builder()
                    .message(e.getMessage())
                    .error("Bad Request")
                    .status(HttpStatus.BAD_REQUEST.value())
                    .build());
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<VacationDTO>> getEmployeeVacations(@PathVariable Long employeeId) {
        return ResponseEntity.ok(vacationService.getEmployeeVacations(employeeId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<VacationDTO>> getVacationsByStatus(@PathVariable VacationStatus status) {
        return ResponseEntity.ok(vacationService.getVacationsByStatus(status));
    }

    @GetMapping("/dates")
    public ResponseEntity<List<VacationDTO>> getVacationsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(vacationService.getVacationsBetweenDates(start, end));
    }

    @GetMapping("/employee/{employeeId}/between")
    public ResponseEntity<List<VacationDTO>> getEmployeeVacationsBetweenDates(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(vacationService.getEmployeeVacationsBetweenDates(employeeId, start, end));
    }

    @DeleteMapping("/employee/{employeeId}")
    public ResponseEntity<Void> deleteEmployeeVacations(@PathVariable Long employeeId) {
        vacationService.deleteEmployeeVacations(employeeId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{vacationId}")
    public ResponseEntity<Void> deleteVacation(@PathVariable Long vacationId) {
        vacationService.deleteVacation(vacationId);
        return ResponseEntity.noContent().build();
    }
} 