package com.Gestion.PromiereVersion.repository;

import com.Gestion.PromiereVersion.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByEmployeeId(Long employeeId);
    List<Schedule> findByWorkDateBetween(LocalDate startDate, LocalDate endDate);
    List<Schedule> findByEmployeeIdAndWorkDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);
} 