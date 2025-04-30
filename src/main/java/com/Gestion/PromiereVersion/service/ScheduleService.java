package com.Gestion.PromiereVersion.service;

import com.Gestion.PromiereVersion.dto.ScheduleDTO;
import com.Gestion.PromiereVersion.model.Schedule;
import com.Gestion.PromiereVersion.model.User;
import com.Gestion.PromiereVersion.repository.ScheduleRepository;
import com.Gestion.PromiereVersion.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Transactional
    public ScheduleDTO createSchedule(ScheduleDTO scheduleDTO) {
        User employee = userRepository.findById(scheduleDTO.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Schedule schedule = Schedule.builder()
                .employee(employee)
                .workDate(scheduleDTO.getWorkDate())
                .startTime(scheduleDTO.getStartTime())
                .endTime(scheduleDTO.getEndTime())
                .notes(scheduleDTO.getNotes())
                .build();

        // Calculer le total des heures
        long minutes = ChronoUnit.MINUTES.between(scheduleDTO.getStartTime(), scheduleDTO.getEndTime());
        schedule.setTotalHours(minutes / 60.0);

        Schedule savedSchedule = scheduleRepository.save(schedule);
        return ScheduleDTO.fromSchedule(savedSchedule);
    }

    public List<ScheduleDTO> getEmployeeSchedules(Long employeeId) {
        return scheduleRepository.findByEmployeeId(employeeId).stream()
                .map(ScheduleDTO::fromSchedule)
                .collect(Collectors.toList());
    }

    public List<ScheduleDTO> getSchedulesByDateRange(LocalDate startDate, LocalDate endDate) {
        return scheduleRepository.findByWorkDateBetween(startDate, endDate).stream()
                .map(ScheduleDTO::fromSchedule)
                .collect(Collectors.toList());
    }

    public List<ScheduleDTO> getEmployeeSchedulesByDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return scheduleRepository.findByEmployeeIdAndWorkDateBetween(employeeId, startDate, endDate).stream()
                .map(ScheduleDTO::fromSchedule)
                .collect(Collectors.toList());
    }

    @Transactional
    public ScheduleDTO updateSchedule(Long id, ScheduleDTO scheduleDTO) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        schedule.setWorkDate(scheduleDTO.getWorkDate());
        schedule.setStartTime(scheduleDTO.getStartTime());
        schedule.setEndTime(scheduleDTO.getEndTime());
        schedule.setNotes(scheduleDTO.getNotes());

        // Recalculer le total des heures
        long minutes = ChronoUnit.MINUTES.between(scheduleDTO.getStartTime(), scheduleDTO.getEndTime());
        schedule.setTotalHours(minutes / 60.0);

        Schedule updatedSchedule = scheduleRepository.save(schedule);
        return ScheduleDTO.fromSchedule(updatedSchedule);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }
} 