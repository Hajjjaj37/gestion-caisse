package com.Gestion.PromiereVersion.controller;

import com.Gestion.PromiereVersion.model.Employee;
import com.Gestion.PromiereVersion.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Employee> createEmployee(
            @RequestParam Long userId,
            @RequestParam String position,
            @RequestParam String department) {
        Employee employee = employeeService.createEmployee(userId, position, department);
        return ResponseEntity.ok(employee);
    }
} 