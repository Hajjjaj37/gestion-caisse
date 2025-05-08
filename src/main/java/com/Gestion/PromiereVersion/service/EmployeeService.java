package com.Gestion.PromiereVersion.service;

import com.Gestion.PromiereVersion.model.Employee;
import com.Gestion.PromiereVersion.model.User;
import com.Gestion.PromiereVersion.repository.EmployeeRepository;
import com.Gestion.PromiereVersion.repository.UserRepository;
import com.Gestion.PromiereVersion.dto.EmployeeWithSchedulesDTO;
import com.Gestion.PromiereVersion.dto.ScheduleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final ScheduleService scheduleService;

    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }

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

    @Transactional
    public void deleteEmployeeByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Employee employee = employeeRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Employee not found for user"));
        
        employeeRepository.delete(employee);
        log.info("Employee deleted successfully for user ID: {}", userId);
    }

    public List<Employee> getAllEmployeesWithBreaks() {
        List<Employee> employees = employeeRepository.findAll();
        // Les pauses sont automatiquement chargées grâce à la relation @OneToMany dans l'entité Employee
        return employees;
    }

    public List<EmployeeWithSchedulesDTO> getAllEmployeesWithSchedules() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map(employee -> {
                    List<ScheduleDTO> schedules = scheduleService.getEmployeeSchedules(employee.getId());
                    return EmployeeWithSchedulesDTO.fromEmployee(employee, schedules);
                })
                .collect(Collectors.toList());
    }
} 