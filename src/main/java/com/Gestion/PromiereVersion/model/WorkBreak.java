package com.Gestion.PromiereVersion.model;

import com.Gestion.PromiereVersion.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "work_breaks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkBreak {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BreakType breakType;

    private String comment;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BreakStatus status;
} 