package com.Gestion.PromiereVersion.dto;

import com.Gestion.PromiereVersion.model.RecurringSchedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecurringScheduleDTO {
    private Long id;
    private Long employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> daysOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String notes;
    private List<RecurringBreakDTO> recurringBreaks;

    public static RecurringScheduleDTO fromRecurringSchedule(RecurringSchedule recurringSchedule) {
        return RecurringScheduleDTO.builder()
                .id(recurringSchedule.getId())
                .employeeId(recurringSchedule.getEmployee() != null ? recurringSchedule.getEmployee().getId() : null)
                .startDate(recurringSchedule.getStartDate())
                .endDate(recurringSchedule.getEndDate())
                .daysOfWeek(recurringSchedule.getWorkingDays())
                .startTime(recurringSchedule.getStartTime())
                .endTime(recurringSchedule.getEndTime())
                .notes(recurringSchedule.getNotes())
                .recurringBreaks(recurringSchedule.getBreaks() != null ? 
                    recurringSchedule.getBreaks().stream()
                        .map(RecurringBreakDTO::fromRecurringBreak)
                        .toList() : null)
                .build();
    }
} 