package com.Gestion.PromiereVersion.controller;

import com.Gestion.PromiereVersion.dto.AuthenticationRequest;
import com.Gestion.PromiereVersion.dto.AuthenticationResponse;
import com.Gestion.PromiereVersion.dto.ErrorResponse;
import com.Gestion.PromiereVersion.dto.RegisterRequest;
import com.Gestion.PromiereVersion.model.Role;
import com.Gestion.PromiereVersion.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            log.info("Attempting to register user with email: {}", request.getEmail());
            // Ne pas forcer le rôle USER si un rôle est spécifié
            if (request.getRole() == null) {
                request.setRole(Role.USER);
            }
            log.info("Registering user with role: {}", request.getRole());
            AuthenticationResponse response = authenticationService.register(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Registration error: {}", e.getMessage());
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .message(e.getMessage())
                    .error("Bad Request")
                    .status(HttpStatus.BAD_REQUEST.value())
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            log.info("Attempting to login user with email: {}", request.getEmail());
            AuthenticationResponse response = authenticationService.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Login error: {}", e.getMessage());
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .message(e.getMessage())
                    .error("Bad Request")
                    .status(HttpStatus.BAD_REQUEST.value())
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody RegisterRequest request) {
        try {
            log.info("Attempting to register admin with email: {}", request.getEmail());
            // Forcer le rôle ADMIN
            request.setRole(Role.ADMIN);
            AuthenticationResponse response = authenticationService.register(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Admin registration error: {}", e.getMessage());
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .message(e.getMessage())
                    .error("Bad Request")
                    .status(HttpStatus.BAD_REQUEST.value())
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
} 