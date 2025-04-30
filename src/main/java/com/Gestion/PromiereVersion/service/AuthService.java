package com.Gestion.PromiereVersion.service;

import com.Gestion.PromiereVersion.model.User;
import com.Gestion.PromiereVersion.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public User authenticate(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordService.verifyPassword(password, user.getPassword()))
                .orElse(null);
    }
} 