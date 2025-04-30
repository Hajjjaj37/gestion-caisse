package com.Gestion.PromiereVersion.service;

import com.Gestion.PromiereVersion.dto.VacationDTO;
import com.Gestion.PromiereVersion.model.User;
import com.Gestion.PromiereVersion.model.Vacation;
import com.Gestion.PromiereVersion.model.VacationStatus;
import com.Gestion.PromiereVersion.repository.UserRepository;
import com.Gestion.PromiereVersion.repository.VacationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacationService {

    private final VacationRepository vacationRepository;
    private final UserRepository userRepository;

    @Transactional
    public VacationDTO createVacation(VacationDTO vacationDTO) {
        log.info("Creating vacation for employee: {}", vacationDTO.getEmployeeId());
        
        User employee = userRepository.findById(vacationDTO.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        if (vacationDTO.getEndDate().isBefore(vacationDTO.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        int duration = (int) ChronoUnit.DAYS.between(vacationDTO.getStartDate(), vacationDTO.getEndDate()) + 1;

        Vacation vacation = Vacation.builder()
                .employee(employee)
                .startDate(vacationDTO.getStartDate())
                .endDate(vacationDTO.getEndDate())
                .duration(duration)
                .status(VacationStatus.PENDING)
                .comment(vacationDTO.getComment())
                .build();

        Vacation savedVacation = vacationRepository.save(vacation);
        return VacationDTO.fromVacation(savedVacation);
    }

    @Transactional
    public VacationDTO updateVacationStatus(Long id, VacationStatus status, String comment) {
        log.info("Updating vacation status for id {}: {}", id, status);

        Vacation vacation = vacationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vacation not found"));

        vacation.setStatus(status);
        vacation.setComment(comment);

        Vacation updatedVacation = vacationRepository.save(vacation);
        return VacationDTO.fromVacation(updatedVacation);
    }

    public List<VacationDTO> getEmployeeVacations(Long employeeId) {
        return vacationRepository.findByEmployeeId(employeeId).stream()
                .map(VacationDTO::fromVacation)
                .collect(Collectors.toList());
    }

    public List<VacationDTO> getVacationsByStatus(VacationStatus status) {
        return vacationRepository.findByStatus(status).stream()
                .map(VacationDTO::fromVacation)
                .collect(Collectors.toList());
    }

    public List<VacationDTO> getVacationsBetweenDates(LocalDate start, LocalDate end) {
        return vacationRepository.findByStartDateBetween(start, end).stream()
                .map(VacationDTO::fromVacation)
                .collect(Collectors.toList());
    }

    public List<VacationDTO> getEmployeeVacationsBetweenDates(Long employeeId, LocalDate start, LocalDate end) {
        return vacationRepository.findByEmployeeIdAndStartDateBetween(employeeId, start, end).stream()
                .map(VacationDTO::fromVacation)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteEmployeeVacations(Long employeeId) {
        log.info("Deleting all vacations for employee: {}", employeeId);
        List<Vacation> vacations = vacationRepository.findByEmployeeId(employeeId);
        vacationRepository.deleteAll(vacations);
        log.info("Deleted {} vacations for employee {}", vacations.size(), employeeId);
    }

    @Transactional
    public void deleteVacation(Long vacationId) {
        log.info("Deleting vacation: {}", vacationId);
        if (!vacationRepository.existsById(vacationId)) {
            throw new IllegalArgumentException("Vacation not found");
        }
        vacationRepository.deleteById(vacationId);
        log.info("Vacation {} deleted successfully", vacationId);
    }
} 