package com.Gestion.PromiereVersion.service;

import com.Gestion.PromiereVersion.model.Break;
import com.Gestion.PromiereVersion.repository.BreakRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BreakService {
    private final BreakRepository breakRepository;

    public BreakService(BreakRepository breakRepository) {
        this.breakRepository = breakRepository;
    }

    public List<Break> findAll() {
        return breakRepository.findAll();
    }

    public Optional<Break> findById(Long id) {
        return breakRepository.findById(id);
    }

    public Break save(Break breakEntity) {
        return breakRepository.save(breakEntity);
    }

    public void delete(Break break_) {
        breakRepository.delete(break_);
    }
} 