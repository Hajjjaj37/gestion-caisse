package com.Gestion.PromiereVersion.controller;

import com.Gestion.PromiereVersion.dto.ErrorResponse;
import com.Gestion.PromiereVersion.dto.MessageResponse;
import com.Gestion.PromiereVersion.dto.VacationDTO;
import com.Gestion.PromiereVersion.model.Employee;
import com.Gestion.PromiereVersion.model.VacationStatus;
import com.Gestion.PromiereVersion.service.EmployeeService;
import com.Gestion.PromiereVersion.service.VacationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/vacations")
@RequiredArgsConstructor
public class VacationController {

    private final VacationService vacationService;
    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<VacationDTO>> getAllVacations() {
        return ResponseEntity.ok(vacationService.getAllVacations());
    }

    @PostMapping
    public ResponseEntity<?> createVacation(@RequestBody VacationDTO vacationDTO) {
        try {
            log.info("Tentative de création d'une demande de vacances: {}", vacationDTO);
            Employee employee = employeeService.findById(vacationDTO.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employé non trouvé avec l'ID: " + vacationDTO.getEmployeeId()));
            
            vacationDTO.setEmployeeId(employee.getId());
            VacationDTO createdVacation = vacationService.createVacation(vacationDTO);
            return ResponseEntity.ok(createdVacation);
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de la création de la demande de vacances: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVacation(@PathVariable Long id, @RequestBody VacationDTO vacationDTO) {
        try {
            log.info("Tentative de modification de la demande de vacances {}: {}", id, vacationDTO);
            Employee employee = employeeService.findById(vacationDTO.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employé non trouvé avec l'ID: " + vacationDTO.getEmployeeId()));
            
            vacationDTO.setEmployeeId(employee.getId());
            VacationDTO updatedVacation = vacationService.updateVacation(id, vacationDTO);
            return ResponseEntity.ok(updatedVacation);
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de la modification de la demande de vacances: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
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
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getVacationsByEmployee(@PathVariable Long employeeId) {
        try {
            // Vérifier si l'employé existe
            Optional<Employee> employeeOpt = employeeService.findById(employeeId);
            if (employeeOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Employee not found with id: " + employeeId));
            }
            
            List<VacationDTO> vacations = vacationService.getVacationsByEmployee(employeeId);
            return ResponseEntity.ok(vacations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error retrieving vacations: " + e.getMessage()));
        }
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

    @GetMapping("/employee/{employeeId}/dates")
    public ResponseEntity<List<VacationDTO>> getEmployeeVacationsBetweenDates(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(vacationService.getEmployeeVacationsBetweenDates(employeeId, start, end));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVacationById(@PathVariable Long id) {
        try {
            VacationDTO vacation = vacationService.getVacationById(id);
            if (vacation == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Vacation not found with id: " + id));
            }
            return ResponseEntity.ok(vacation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error retrieving vacation: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVacation(@PathVariable Long id) {
        try {
            vacationService.deleteVacation(id);
            return ResponseEntity.ok(new MessageResponse("Vacation deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error deleting vacation: " + e.getMessage()));
        }
    }
} 