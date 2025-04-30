package com.Gestion.PromiereVersion.service;

import com.Gestion.PromiereVersion.dto.AuthenticationRequest;
import com.Gestion.PromiereVersion.dto.AuthenticationResponse;
import com.Gestion.PromiereVersion.dto.RegisterRequest;
import com.Gestion.PromiereVersion.model.Role;
import com.Gestion.PromiereVersion.model.User;
import com.Gestion.PromiereVersion.repository.UserRepository;
import com.Gestion.PromiereVersion.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmployeeService employeeService;

    public AuthenticationResponse register(RegisterRequest request) {
        // Validation des données
        if (!StringUtils.hasText(request.getEmail()) || !StringUtils.hasText(request.getPassword())) {
            throw new RuntimeException("Email and password are required");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Utiliser le rôle spécifié dans la requête
        Role userRole = request.getRole();
        log.info("Creating user with role: {}", userRole);

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .build();

        log.info("Saving user with role: {}", user.getRole());
        userRepository.save(user);
        
        // Si le rôle est EMPLOYEE, créer automatiquement un employé
        if (userRole == Role.EMPLOYEE) {
            try {
                employeeService.createEmployeeFromUser(user);
                log.info("Employee created automatically for user: {}", user.getEmail());
            } catch (Exception e) {
                log.error("Error creating employee for user: {}", user.getEmail(), e);
                // Ne pas bloquer l'enregistrement de l'utilisateur si la création de l'employé échoue
            }
        }
        
        // Générer le token JWT
        var jwtToken = jwtService.generateToken(user);
        
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            // Validation des données
            if (!StringUtils.hasText(request.getEmail()) || !StringUtils.hasText(request.getPassword())) {
                throw new RuntimeException("Email and password are required");
            }

            log.info("Attempting to authenticate user with email: {}", request.getEmail());
            
            // Vérifier d'abord si l'utilisateur existe
            var userOptional = userRepository.findByEmail(request.getEmail().trim());
            log.info("User found in database: {}", userOptional.isPresent());
            
            if (userOptional.isEmpty()) {
                log.error("User not found with email: {}", request.getEmail());
                throw new RuntimeException("User not found");
            }

            var user = userOptional.get();
            log.info("User found: {}", user.getEmail());

            // Vérifier le mot de passe
            boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
            log.info("Password matches: {}", passwordMatches);
            
            if (!passwordMatches) {
                throw new RuntimeException("Invalid password");
            }

            // Authentifier avec Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail().trim(),
                            request.getPassword()
                    )
            );

            // Générer le token JWT
            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        } catch (RuntimeException e) {
            log.error("Authentication error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during authentication: {}", e.getMessage());
            throw new RuntimeException("Authentication failed");
        }
    }
} 