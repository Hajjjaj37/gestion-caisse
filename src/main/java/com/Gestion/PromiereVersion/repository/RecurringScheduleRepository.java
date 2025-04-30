package com.Gestion.PromiereVersion.repository;

import com.Gestion.PromiereVersion.model.RecurringSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecurringScheduleRepository extends JpaRepository<RecurringSchedule, Long> {
    List<RecurringSchedule> findByEmployeeId(Long employeeId);
} 