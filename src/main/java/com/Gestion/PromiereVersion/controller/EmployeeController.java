package com.Gestion.PromiereVersion.controller;

import com.Gestion.PromiereVersion.model.Employee;
import com.Gestion.PromiereVersion.service.EmployeeService;
import com.Gestion.PromiereVersion.dto.EmployeeWithSchedulesDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployeesWithBreaks();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.findById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        return ResponseEntity.ok(employee);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Employee> createEmployee(
            @RequestParam Long userId,
            @RequestParam String position,
            @RequestParam String department) {
        Employee employee = employeeService.createEmployee(userId, position, department);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/with-breaks")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Employee>> getAllEmployeesWithBreaks() {
        List<Employee> employees = employeeService.getAllEmployeesWithBreaks();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/with-schedules")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EmployeeWithSchedulesDTO>> getAllEmployeesWithSchedules() {
        List<EmployeeWithSchedulesDTO> employees = employeeService.getAllEmployeesWithSchedules();
        return ResponseEntity.ok(employees);
    }
} 