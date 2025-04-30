package com.Gestion.PromiereVersion.service;

import com.Gestion.PromiereVersion.dto.CreateRecurringBreakDTO;
import com.Gestion.PromiereVersion.model.RecurringBreak;
import com.Gestion.PromiereVersion.model.RecurringSchedule;
import com.Gestion.PromiereVersion.repository.RecurringBreakRepository;
import com.Gestion.PromiereVersion.repository.RecurringScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecurringBreakService {

    private final RecurringBreakRepository recurringBreakRepository;
    private final RecurringScheduleRepository recurringScheduleRepository;

    @Transactional
    public RecurringBreak createRecurringBreak(CreateRecurringBreakDTO dto) {
        RecurringBreak break_ = RecurringBreak.builder()
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .durationMinutes(dto.getDurationMinutes())
                .type(dto.getType())
                .comment(dto.getComment())
                .build();

        return recurringBreakRepository.save(break_);
    }

    @Transactional
    public RecurringBreak assignBreakToSchedule(Long breakId, Long scheduleId) {
        RecurringBreak break_ = recurringBreakRepository.findById(breakId)
                .orElseThrow(() -> new RuntimeException("Break not found"));
        
        RecurringSchedule schedule = recurringScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        break_.setRecurringSchedule(schedule);
        return recurringBreakRepository.save(break_);
    }

    public List<RecurringBreak> getAllRecurringBreaks() {
        return recurringBreakRepository.findAll();
    }

    public RecurringBreak getRecurringBreak(Long id) {
        return recurringBreakRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Break not found"));
    }

    @Transactional
    public void deleteRecurringBreak(Long id) {
        recurringBreakRepository.deleteById(id);
    }
} 