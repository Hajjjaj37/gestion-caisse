package com.Gestion.PromiereVersion.service;

import com.Gestion.PromiereVersion.dto.RecurringScheduleDTO;
import com.Gestion.PromiereVersion.model.Employee;
import com.Gestion.PromiereVersion.model.RecurringBreak;
import com.Gestion.PromiereVersion.model.RecurringSchedule;
import com.Gestion.PromiereVersion.repository.EmployeeRepository;
import com.Gestion.PromiereVersion.repository.RecurringBreakRepository;
import com.Gestion.PromiereVersion.repository.RecurringScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecurringScheduleService {

    private final RecurringScheduleRepository recurringScheduleRepository;
    private final EmployeeRepository employeeRepository;
    private final RecurringBreakRepository recurringBreakRepository;

    @Transactional
    public RecurringScheduleDTO createSchedule(RecurringScheduleDTO scheduleDTO) {
        Employee employee = employeeRepository.findById(scheduleDTO.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        RecurringSchedule schedule = RecurringSchedule.builder()
                .employee(employee)
                .startDate(scheduleDTO.getStartDate())
                .endDate(scheduleDTO.getEndDate())
                .workingDays(scheduleDTO.getDaysOfWeek())
                .startTime(scheduleDTO.getStartTime())
                .endTime(scheduleDTO.getEndTime())
                .notes(scheduleDTO.getNotes())
                .build();

        if (scheduleDTO.getRecurringBreaks() != null) {
            List<RecurringBreak> breaks = scheduleDTO.getRecurringBreaks().stream()
                    .map(breakDTO -> recurringBreakRepository.findById(breakDTO.getId())
                            .orElseThrow(() -> new EntityNotFoundException("Break not found with id: " + breakDTO.getId())))
                    .collect(Collectors.toList());
            schedule.setBreaks(breaks);
        }

        RecurringSchedule savedSchedule = recurringScheduleRepository.save(schedule);
        return RecurringScheduleDTO.fromRecurringSchedule(savedSchedule);
    }

    @Transactional(readOnly = true)
    public RecurringScheduleDTO getSchedule(Long id) {
        RecurringSchedule schedule = recurringScheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
        return RecurringScheduleDTO.fromRecurringSchedule(schedule);
    }

    @Transactional(readOnly = true)
    public List<RecurringScheduleDTO> getAllSchedules() {
        return recurringScheduleRepository.findAll().stream()
                .map(RecurringScheduleDTO::fromRecurringSchedule)
                .collect(Collectors.toList());
    }

    @Transactional
    public RecurringScheduleDTO updateSchedule(Long id, RecurringScheduleDTO scheduleDTO) {
        RecurringSchedule schedule = recurringScheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));

        Employee employee = employeeRepository.findById(scheduleDTO.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        schedule.setEmployee(employee);
        schedule.setStartDate(scheduleDTO.getStartDate());
        schedule.setEndDate(scheduleDTO.getEndDate());
        schedule.setWorkingDays(scheduleDTO.getDaysOfWeek());
        schedule.setStartTime(scheduleDTO.getStartTime());
        schedule.setEndTime(scheduleDTO.getEndTime());
        schedule.setNotes(scheduleDTO.getNotes());

        if (scheduleDTO.getRecurringBreaks() != null) {
            List<RecurringBreak> breaks = scheduleDTO.getRecurringBreaks().stream()
                    .map(breakDTO -> recurringBreakRepository.findById(breakDTO.getId())
                            .orElseThrow(() -> new EntityNotFoundException("Break not found with id: " + breakDTO.getId())))
                    .collect(Collectors.toList());
            schedule.setBreaks(breaks);
        }

        RecurringSchedule updatedSchedule = recurringScheduleRepository.save(schedule);
        return RecurringScheduleDTO.fromRecurringSchedule(updatedSchedule);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        if (!recurringScheduleRepository.existsById(id)) {
            throw new EntityNotFoundException("Schedule not found");
        }
        recurringScheduleRepository.deleteById(id);
    }
} 