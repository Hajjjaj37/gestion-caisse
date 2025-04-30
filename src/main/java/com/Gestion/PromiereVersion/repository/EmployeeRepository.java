package com.Gestion.PromiereVersion.repository;

import com.Gestion.PromiereVersion.model.Employee;
import com.Gestion.PromiereVersion.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByUser(User user);
    Optional<Employee> findByUser(User user);
} 