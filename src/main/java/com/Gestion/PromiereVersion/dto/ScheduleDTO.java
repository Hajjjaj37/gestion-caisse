package com.Gestion.PromiereVersion.dto;

import com.Gestion.PromiereVersion.model.Schedule;
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
public class ScheduleDTO {
    private Long id;
    private Long employeeId;
    private LocalDate workDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double totalHours;
    private List<ScheduleBreakDTO> breaks;
    private String notes;

    public static ScheduleDTO fromSchedule(Schedule schedule) {
        return ScheduleDTO.builder()
                .id(schedule.getId())
                .employeeId(schedule.getEmployee().getId())
                .workDate(schedule.getWorkDate())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .totalHours(schedule.getTotalHours())
                .notes(schedule.getNotes())
                .build();
    }
} 