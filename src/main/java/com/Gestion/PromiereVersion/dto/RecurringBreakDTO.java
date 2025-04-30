package com.Gestion.PromiereVersion.dto;

import com.Gestion.PromiereVersion.model.RecurringBreak;
import com.Gestion.PromiereVersion.model.BreakType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecurringBreakDTO {
    private Long id;
    private Long recurringScheduleId;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationMinutes;
    private BreakType type;
    private String comment;

    public static RecurringBreakDTO fromRecurringBreak(RecurringBreak recurringBreak) {
        return RecurringBreakDTO.builder()
                .id(recurringBreak.getId())
                .recurringScheduleId(recurringBreak.getRecurringSchedule() != null ? recurringBreak.getRecurringSchedule().getId() : null)
                .startTime(recurringBreak.getStartTime())
                .endTime(recurringBreak.getEndTime())
                .durationMinutes(recurringBreak.getDurationMinutes())
                .type(recurringBreak.getType())
                .comment(recurringBreak.getComment())
                .build();
    }
} 