package com.Gestion.PromiereVersion.dto;

import com.Gestion.PromiereVersion.model.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeWithSchedulesDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String position;
    private String department;
    private List<ScheduleDTO> schedules;

    public static EmployeeWithSchedulesDTO fromEmployee(Employee employee, List<ScheduleDTO> schedules) {
        return EmployeeWithSchedulesDTO.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .position(employee.getPosition())
                .department(employee.getDepartment())
                .schedules(schedules)
                .build();
    }
} 