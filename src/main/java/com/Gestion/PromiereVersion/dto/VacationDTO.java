package com.Gestion.PromiereVersion.dto;

import com.Gestion.PromiereVersion.model.Vacation;
import com.Gestion.PromiereVersion.model.VacationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VacationDTO {
    private Long id;
    private Long employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer duration;
    private VacationStatus status;
    private String comment;

    public static VacationDTO fromVacation(Vacation vacation) {
        return VacationDTO.builder()
                .id(vacation.getId())
                .employeeId(vacation.getEmployee().getId())
                .startDate(vacation.getStartDate())
                .endDate(vacation.getEndDate())
                .duration(vacation.getDuration())
                .status(vacation.getStatus())
                .comment(vacation.getComment())
                .build();
    }
} 