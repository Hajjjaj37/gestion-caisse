package com.Gestion.PromiereVersion.repository;

import com.Gestion.PromiereVersion.model.RecurringBreak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecurringBreakRepository extends JpaRepository<RecurringBreak, Long> {
} 