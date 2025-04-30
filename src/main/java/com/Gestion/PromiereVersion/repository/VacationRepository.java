package com.Gestion.PromiereVersion.repository;

import com.Gestion.PromiereVersion.model.Vacation;
import com.Gestion.PromiereVersion.model.VacationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VacationRepository extends JpaRepository<Vacation, Long> {
    List<Vacation> findByEmployeeId(Long employeeId);
    List<Vacation> findByStatus(VacationStatus status);
    List<Vacation> findByStartDateBetween(LocalDate start, LocalDate end);
    List<Vacation> findByEmployeeIdAndStartDateBetween(Long employeeId, LocalDate start, LocalDate end);
} 