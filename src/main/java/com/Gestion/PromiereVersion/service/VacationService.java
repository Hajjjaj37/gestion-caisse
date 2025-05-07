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

    public List<VacationDTO> getAllVacations() {
        return vacationRepository.findAll().stream()
                .map(VacationDTO::fromVacation)
                .collect(Collectors.toList());
    }

    @Transactional
    public VacationDTO createVacation(VacationDTO vacationDTO) {
        log.info("Création d'une nouvelle demande de vacances: {}", vacationDTO);

        // Validation des dates
        if (vacationDTO.getStartDate().isAfter(vacationDTO.getEndDate())) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }

        // Calcul de la durée
        long days = ChronoUnit.DAYS.between(vacationDTO.getStartDate(), vacationDTO.getEndDate()) + 1;
        vacationDTO.setDuration((int) days);

        // Récupération de l'employé
        User employee = userRepository.findById(vacationDTO.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employé non trouvé"));

        // Création de la demande de vacances
        Vacation vacation = Vacation.builder()
                .employee(employee)
                .startDate(vacationDTO.getStartDate())
                .endDate(vacationDTO.getEndDate())
                .duration(vacationDTO.getDuration())
                .status(VacationStatus.PENDING)
                .comment(vacationDTO.getComment())
                .build();

        // Sauvegarde
        Vacation savedVacation = vacationRepository.save(vacation);
        log.info("Demande de vacances créée avec succès: {}", savedVacation);

        return VacationDTO.fromVacation(savedVacation);
    }

    @Transactional
    public VacationDTO updateVacation(Long id, VacationDTO vacationDTO) {
        log.info("Mise à jour de la demande de vacances {}: {}", id, vacationDTO);

        // Récupération de la vacance existante
        Vacation vacation = vacationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Demande de vacances non trouvée"));

        // Validation des dates
        if (vacationDTO.getStartDate().isAfter(vacationDTO.getEndDate())) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }

        // Calcul de la durée
        long days = ChronoUnit.DAYS.between(vacationDTO.getStartDate(), vacationDTO.getEndDate()) + 1;
        vacationDTO.setDuration((int) days);

        // Mise à jour des champs
        vacation.setStartDate(vacationDTO.getStartDate());
        vacation.setEndDate(vacationDTO.getEndDate());
        vacation.setDuration(vacationDTO.getDuration());
        vacation.setComment(vacationDTO.getComment());
        vacation.setStatus(VacationStatus.PENDING); // Réinitialiser le statut à PENDING lors de la modification

        // Sauvegarde
        Vacation updatedVacation = vacationRepository.save(vacation);
        log.info("Demande de vacances mise à jour avec succès: {}", updatedVacation);

        return VacationDTO.fromVacation(updatedVacation);
    }

    @Transactional
    public VacationDTO updateVacationStatus(Long id, VacationStatus status, String comment) {
        log.info("Mise à jour du statut de la demande de vacances {}: {}", id, status);

        Vacation vacation = vacationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Demande de vacances non trouvée"));

        vacation.setStatus(status);
        vacation.setComment(comment);

        Vacation updatedVacation = vacationRepository.save(vacation);
        log.info("Statut de la demande de vacances mis à jour: {}", updatedVacation);

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
        log.info("Récupération des vacances de l'employé {} entre {} et {}", employeeId, start, end);
        List<Vacation> vacations = vacationRepository.findByEmployeeIdAndStartDateBetween(employeeId, start, end);
        return vacations.stream()
            .map(VacationDTO::fromVacation)
            .collect(Collectors.toList());
    }

    public List<VacationDTO> getVacationsByEmployee(Long employeeId) {
        log.info("Récupération des vacances pour l'employé ID: {}", employeeId);
        List<Vacation> vacations = vacationRepository.findByEmployeeId(employeeId);
        return vacations.stream()
                .map(VacationDTO::fromVacation)
                .collect(Collectors.toList());
    }

    public VacationDTO getVacationById(Long id) {
        log.info("Récupération de la vacance avec l'ID: {}", id);
        return vacationRepository.findById(id)
            .map(VacationDTO::fromVacation)
            .orElse(null);
    }

    @Transactional
    public void deleteVacation(Long id) {
        log.info("Suppression de la vacance avec l'ID: {}", id);
        if (!vacationRepository.existsById(id)) {
            throw new IllegalArgumentException("Vacation not found with id: " + id);
        }
        vacationRepository.deleteById(id);
    }
} 