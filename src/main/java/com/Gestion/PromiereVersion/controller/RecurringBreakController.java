package com.Gestion.PromiereVersion.controller;

import com.Gestion.PromiereVersion.dto.CreateRecurringBreakDTO;
import com.Gestion.PromiereVersion.dto.RecurringBreakDTO;
import com.Gestion.PromiereVersion.model.RecurringBreak;
import com.Gestion.PromiereVersion.service.RecurringBreakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recurring-breaks")
@RequiredArgsConstructor
public class RecurringBreakController {

    private final RecurringBreakService recurringBreakService;

    @PostMapping
    public ResponseEntity<RecurringBreakDTO> createRecurringBreak(
            @RequestBody CreateRecurringBreakDTO createRecurringBreakDTO) {
        RecurringBreak break_ = recurringBreakService.createRecurringBreak(createRecurringBreakDTO);
        return ResponseEntity.ok(RecurringBreakDTO.fromRecurringBreak(break_));
    }

    @PostMapping("/{breakId}/assign-to-schedule/{scheduleId}")
    public ResponseEntity<RecurringBreakDTO> assignBreakToSchedule(
            @PathVariable Long breakId,
            @PathVariable Long scheduleId) {
        RecurringBreak break_ = recurringBreakService.assignBreakToSchedule(breakId, scheduleId);
        return ResponseEntity.ok(RecurringBreakDTO.fromRecurringBreak(break_));
    }

    @GetMapping
    public ResponseEntity<List<RecurringBreakDTO>> getAllRecurringBreaks() {
        List<RecurringBreak> breaks = recurringBreakService.getAllRecurringBreaks();
        return ResponseEntity.ok(breaks.stream()
                .map(RecurringBreakDTO::fromRecurringBreak)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecurringBreakDTO> getRecurringBreak(@PathVariable Long id) {
        RecurringBreak break_ = recurringBreakService.getRecurringBreak(id);
        return ResponseEntity.ok(RecurringBreakDTO.fromRecurringBreak(break_));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecurringBreak(@PathVariable Long id) {
        recurringBreakService.deleteRecurringBreak(id);
        return ResponseEntity.ok().build();
    }
} 