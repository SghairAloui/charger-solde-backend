package com.chargersolde.service;

import com.chargersolde.dto.RechargePlanDTO;
import com.chargersolde.entity.Operator;
import com.chargersolde.entity.RechargePlan;
import com.chargersolde.repository.OperatorRepository;
import com.chargersolde.repository.RechargePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RechargePlanService {

    private final RechargePlanRepository repo;
    private final OperatorRepository operatorRepository;

    public RechargePlan create(RechargePlanDTO dto) {

        Operator operator = operatorRepository.findById(dto.getOperatorId())
                .orElseThrow(() -> new RuntimeException("Operator not found"));

        RechargePlan plan = RechargePlan.builder()
                .label(dto.getLabel())
                .price(dto.getPrice())
                .validityDays(dto.getValidityDays())
                .operator(operator)
                .build();

        return repo.save(plan);
    }

    public List<RechargePlan> getByOperator(Long operatorId) {
        return repo.findByOperatorId(operatorId);
    }
}