package com.Gestion.PromiereVersion.dto;

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
public class CreateRecurringBreakDTO {
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationMinutes;
    private BreakType type;  // Les valeurs possibles sont : PAUSE_PERSONNELLE, PAUSE_DEJEUNER, PAUSE_CAFE, PAUSE_SANTE
    private String comment;
} 