package com.Gestion.PromiereVersion.dto;

import com.Gestion.PromiereVersion.model.BreakType;
import com.Gestion.PromiereVersion.model.ScheduleBreak;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleBreakDTO {
    private Long id;
    private Long scheduleId;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationMinutes;
    private BreakType type;
    private String comment;

    public static ScheduleBreakDTO fromScheduleBreak(ScheduleBreak scheduleBreak) {
        return ScheduleBreakDTO.builder()
                .id(scheduleBreak.getId())
                .scheduleId(scheduleBreak.getSchedule().getId())
                .startTime(scheduleBreak.getStartTime())
                .endTime(scheduleBreak.getEndTime())
                .durationMinutes(scheduleBreak.getDurationMinutes())
                .type(scheduleBreak.getType())
                .comment(scheduleBreak.getComment())
                .build();
    }
} 