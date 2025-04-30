package com.Gestion.PromiereVersion.service;

import com.Gestion.PromiereVersion.model.Employee;
import com.Gestion.PromiereVersion.model.User;
import com.Gestion.PromiereVersion.repository.EmployeeRepository;
import com.Gestion.PromiereVersion.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    @Transactional
    public Employee createEmployee(Long userId, String position, String department) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Employee employee = Employee.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .user(user)
                .position(position)
                .department(department)
                .build();

        return employeeRepository.save(employee);
    }

    public Employee createEmployeeFromUser(User user) {
        log.info("Starting employee creation for user: {}", user.getEmail());
        
        // Vérifier si un employé existe déjà pour cet utilisateur
        if (employeeRepository.existsByUser(user)) {
            log.warn("Employee already exists for user: {}", user.getEmail());
            throw new RuntimeException("Employee already exists for user: " + user.getEmail());
        }

        log.info("Building employee object for user: {}", user.getEmail());
        Employee employee = Employee.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .position("Employee") // Position par défaut
                .department("General") // Département par défaut
                .user(user)
                .build();
        
        log.info("Saving employee for user: {}", user.getEmail());
        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee saved successfully with ID: {}", savedEmployee.getId());
        
        // Vérifier que l'employé a bien été sauvegardé
        Employee retrievedEmployee = employeeRepository.findById(savedEmployee.getId())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve saved employee"));
        log.info("Verified employee exists in database with ID: {}", retrievedEmployee.getId());
        
        return savedEmployee;
    }
} 