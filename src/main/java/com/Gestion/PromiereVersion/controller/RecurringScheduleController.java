package com.Gestion.PromiereVersion.controller;

import com.Gestion.PromiereVersion.dto.RecurringScheduleDTO;
import com.Gestion.PromiereVersion.service.RecurringScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recurring-schedules")
@RequiredArgsConstructor
public class RecurringScheduleController {

    private final RecurringScheduleService recurringScheduleService;

    @PostMapping
    public ResponseEntity<RecurringScheduleDTO> createSchedule(@RequestBody RecurringScheduleDTO scheduleDTO) {
        RecurringScheduleDTO createdSchedule = recurringScheduleService.createSchedule(scheduleDTO);
        return ResponseEntity.ok(createdSchedule);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecurringScheduleDTO> getSchedule(@PathVariable Long id) {
        RecurringScheduleDTO schedule = recurringScheduleService.getSchedule(id);
        return ResponseEntity.ok(schedule);
    }

    @GetMapping
    public ResponseEntity<List<RecurringScheduleDTO>> getAllSchedules() {
        List<RecurringScheduleDTO> schedules = recurringScheduleService.getAllSchedules();
        return ResponseEntity.ok(schedules);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecurringScheduleDTO> updateSchedule(
            @PathVariable Long id,
            @RequestBody RecurringScheduleDTO scheduleDTO) {
        RecurringScheduleDTO updatedSchedule = recurringScheduleService.updateSchedule(id, scheduleDTO);
        return ResponseEntity.ok(updatedSchedule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        recurringScheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
} 