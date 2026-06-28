package com.chargersolde.service;

import com.chargersolde.dto.RechargePlanDTO;
import com.chargersolde.entity.Operator;
import com.chargersolde.entity.RechargePlan;
import com.chargersolde.repository.OperatorRepository;
import com.chargersolde.repository.RechargePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
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
                .active(true) // 👈 ALWAYS ACTIVE ON CREATE

                .operator(operator)
                .build();

        return repo.save(plan);
    }

    public List<RechargePlan> getByOperator(Long operatorId) {
        return repo.findByOperatorId(operatorId);
    }

    public RechargePlan update(Long id, RechargePlanDTO dto) {
        RechargePlan plan = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        Operator operator = operatorRepository.findById(dto.getOperatorId())
                .orElseThrow(() -> new RuntimeException("Operator not found"));

        plan.setLabel(dto.getLabel());
        plan.setPrice(dto.getPrice());
        plan.setValidityDays(dto.getValidityDays());
        plan.setOperator(operator);

        return repo.save(plan);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<RechargePlan> getActiveByOperator(Long operatorId) {
        return repo.findByOperatorIdAndActiveTrue(operatorId);
    }



    public RechargePlan setActive(Long id, boolean active) {
        RechargePlan plan = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        plan.setActive(active);
        return repo.save(plan);
    }
}