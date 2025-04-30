package com.Gestion.PromiereVersion.controller;

import com.Gestion.PromiereVersion.dto.BreakRequest;
import com.Gestion.PromiereVersion.model.Break;
import com.Gestion.PromiereVersion.model.BreakStatus;
import com.Gestion.PromiereVersion.model.User;
import com.Gestion.PromiereVersion.service.BreakService;
import com.Gestion.PromiereVersion.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/breaks")
public class BreakController {

    private final BreakService breakService;
    private final UserService userService;

    public BreakController(BreakService breakService, UserService userService) {
        this.breakService = breakService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Break> startBreak(@RequestBody BreakRequest request) {
        User user = userService.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Vérifier si l'utilisateur a déjà une pause en cours
        boolean hasActiveBreak = user.getBreaks().stream()
                .anyMatch(b -> b.getStatus() == BreakStatus.EN_COURS);
        
        if (hasActiveBreak) {
            throw new RuntimeException("User already has an active break");
        }

        Break newBreak = new Break();
        newBreak.setUser(user);
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
} 