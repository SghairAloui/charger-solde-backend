package com.chargersolde.service;

import com.chargersolde.entity.Operator;
import com.chargersolde.repository.OperatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperatorService {

    private final OperatorRepository repo;

    public List<Operator> getAll() {
        return repo.findAll();
    }

    public Operator create(Operator op) {
        return repo.save(op);
    }
}
