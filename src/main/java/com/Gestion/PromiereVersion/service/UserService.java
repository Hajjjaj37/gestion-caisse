package com.Gestion.PromiereVersion.service;

import com.Gestion.PromiereVersion.model.Role;
import com.Gestion.PromiereVersion.model.User;
import com.Gestion.PromiereVersion.model.Employee;
import com.Gestion.PromiereVersion.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeService employeeService;

    @Transactional
    public User registerUser(String firstName, String lastName, String email, String password, String role) {
        log.info("Starting user registration process for email: {}", email);
        
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        Role userRole = Role.valueOf(role);
        log.info("Creating user with role: {}", userRole);

        // Générer un nom d'utilisateur à partir de l'email
        String username = email.split("@")[0];

        var user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .email(email.trim())
                .password(passwordEncoder.encode(password))
                .role(userRole)
                .build();

        log.info("Saving user with role: {}", user.getRole());
        User savedUser = userRepository.save(user);
        log.info("User saved successfully with ID: {}", savedUser.getId());
        
        // Si le rôle est EMPLOYEE, créer automatiquement un employé
        if (userRole == Role.EMPLOYEE) {
            try {
                log.info("Attempting to create employee for user: {}", savedUser.getEmail());
                if (employeeService == null) {
                    log.error("EmployeeService is null!");
                    throw new RuntimeException("EmployeeService is not properly injected");
                }
                Employee employee = employeeService.createEmployeeFromUser(savedUser);
                log.info("Employee created successfully with ID: {}", employee.getId());
                
                // Rafraîchir l'utilisateur pour s'assurer que les changements sont persistés
                savedUser = userRepository.findById(savedUser.getId())
                        .orElseThrow(() -> new RuntimeException("User not found after employee creation"));
            } catch (Exception e) {
                log.error("Error creating employee for user: {}", savedUser.getEmail(), e);
                throw new RuntimeException("Failed to create employee: " + e.getMessage());
            }
        }
        
        return savedUser;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User updateUser(Long id, String firstName, String lastName, String email, String password) {
        User existingUser = getUserById(id);
        
        // Check if the new email is already taken by another user
        if (!existingUser.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        existingUser.setFirstName(firstName);
        existingUser.setLastName(lastName);
        existingUser.setEmail(email);
        
        // Only update password if a new one is provided
        if (password != null && !password.trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(password));
        }

        return userRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        
        // If the user is an employee, delete the employee record first
        if (user.getRole() == Role.EMPLOYEE) {
            try {
                employeeService.deleteEmployeeByUserId(id);
            } catch (Exception e) {
                log.error("Error deleting employee record for user {}: {}", id, e.getMessage());
                throw new RuntimeException("Failed to delete user: " + e.getMessage());
            }
        }
        
        userRepository.delete(user);
    }
} 