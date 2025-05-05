package com.Gestion.PromiereVersion.dto;

import com.Gestion.PromiereVersion.model.BreakType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BreakRequest {
    private Long employeeId;
    private BreakType type;
    private String comment;
} 