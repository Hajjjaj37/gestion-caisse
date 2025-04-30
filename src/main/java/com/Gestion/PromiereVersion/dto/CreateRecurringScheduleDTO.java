package com.Gestion.PromiereVersion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecurringScheduleDTO {
    private Long employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<DayOfWeek> workingDays;
    private LocalTime startTime;
    private LocalTime endTime;
    private String notes;
    
    // Liste des IDs des pauses existantes à associer à cet horaire
    private List<Long> breakIds;
    
    // Période de travail en mois
    private Integer workPeriodMonths;
} 