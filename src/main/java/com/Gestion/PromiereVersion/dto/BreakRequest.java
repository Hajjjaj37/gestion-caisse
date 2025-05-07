package com.Gestion.PromiereVersion.dto;

import com.Gestion.PromiereVersion.model.BreakStatus;
import com.Gestion.PromiereVersion.model.BreakType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BreakRequest {
    private Long employeeId;
    private BreakType type;
    private BreakStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private String comment;
} 