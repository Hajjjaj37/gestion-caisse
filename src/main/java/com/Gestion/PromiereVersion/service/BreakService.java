package com.Gestion.PromiereVersion.service;

import com.Gestion.PromiereVersion.model.Break;
import com.Gestion.PromiereVersion.repository.BreakRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BreakService {
    private final BreakRepository breakRepository;

    public BreakService(BreakRepository breakRepository) {
        this.breakRepository = breakRepository;
    }

    public Break save(Break breakEntity) {
        return breakRepository.save(breakEntity);
    }

    public Optional<Break> findById(Long id) {
        return breakRepository.findById(id);
    }
} 