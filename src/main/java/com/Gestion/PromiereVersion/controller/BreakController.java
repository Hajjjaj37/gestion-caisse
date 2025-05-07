package com.Gestion.PromiereVersion.controller;

import com.Gestion.PromiereVersion.dto.BreakRequest;
import com.Gestion.PromiereVersion.model.Break;
import com.Gestion.PromiereVersion.model.BreakStatus;
import com.Gestion.PromiereVersion.model.Employee;
import com.Gestion.PromiereVersion.service.BreakService;
import com.Gestion.PromiereVersion.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/breaks")
public class BreakController {

    private final BreakService breakService;
    private final EmployeeService employeeService;

    public BreakController(BreakService breakService, EmployeeService employeeService) {
        this.breakService = breakService;
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<Break> startBreak(@RequestBody BreakRequest request) {
        if (request.getEmployeeId() == null) {
            throw new RuntimeException("Employee ID is required");
        }

        Employee employee = employeeService.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Break newBreak = new Break();
        newBreak.setEmployee(employee);
        newBreak.setType(request.getType());
        newBreak.setStatus(BreakStatus.EN_COURS);
        newBreak.setStartTime(LocalDateTime.now());
        newBreak.setComment(request.getComment());

        Break savedBreak = breakService.save(newBreak);
        return ResponseEntity.ok(savedBreak);
    }

    @PutMapping("/{breakId}/end")
    public ResponseEntity<Break> endBreak(@PathVariable Long breakId) {
        Break existingBreak = breakService.findById(breakId)
                .orElseThrow(() -> new RuntimeException("Break not found"));

        existingBreak.setEndTime(LocalDateTime.now());
        existingBreak.setStatus(BreakStatus.TERMINEE);
        
        // Calculer la durée en minutes
        long durationMinutes = ChronoUnit.MINUTES.between(
            existingBreak.getStartTime(),
            existingBreak.getEndTime()
        );
        existingBreak.setDurationMinutes((int) durationMinutes);

        Break updatedBreak = breakService.save(existingBreak);
        return ResponseEntity.ok(updatedBreak);
    }

    @PutMapping("/{breakId}")
    public ResponseEntity<Break> updateBreak(@PathVariable Long breakId, @RequestBody BreakRequest request) {
        Break existingBreak = breakService.findById(breakId)
                .orElseThrow(() -> new RuntimeException("Break not found"));

        if (request.getType() != null) {
            existingBreak.setType(request.getType());
        }
        if (request.getStatus() != null) {
            existingBreak.setStatus(request.getStatus());
        }
        if (request.getStartTime() != null) {
            existingBreak.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            existingBreak.setEndTime(request.getEndTime());
        }
        if (request.getDurationMinutes() != null) {
            existingBreak.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getComment() != null) {
            existingBreak.setComment(request.getComment());
        }

        Break updatedBreak = breakService.save(existingBreak);
        return ResponseEntity.ok(updatedBreak);
    }

    @DeleteMapping("/{breakId}")
    public ResponseEntity<Void> deleteBreak(@PathVariable Long breakId) {
        Break existingBreak = breakService.findById(breakId)
                .orElseThrow(() -> new RuntimeException("Break not found"));
        
        breakService.delete(existingBreak);
        return ResponseEntity.noContent().build();
    }
} 