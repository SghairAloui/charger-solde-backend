package com.chargersolde.service;

import com.chargersolde.entity.Operator;
import com.chargersolde.entity.RechargePlan;
import com.chargersolde.repository.OperatorRepository;
import com.chargersolde.repository.RechargePlanRepository;
import com.chargersolde.repository.RechargeRequestRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperatorService {

    private final OperatorRepository repo;
    private final RechargePlanRepository planRepo;
    private final RechargeRequestRepository requestRepo;
    private final EntityManager entityManager;

    @PostConstruct
    @Transactional
    public void migrateLogoColumn() {
        try {
            entityManager.createNativeQuery(
                "ALTER TABLE operators ALTER COLUMN logo_url TYPE TEXT"
            ).executeUpdate();
            log.info("Migration: logo_url column updated to TEXT");
        } catch (Exception e) {
            log.info("Migration logo_url déjà appliquée ou ignorée: {}", e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<Operator> getAll() {
        return repo.findAll();
    }

    public Operator create(Operator op) {
        return repo.save(op);
    }

    @Transactional
    public void delete(Long id) {
        Operator op = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Opérateur introuvable"));

        if (op.getPlans() != null) {
            for (RechargePlan plan : op.getPlans()) {
                // Supprimer toutes les demandes de recharge liées à ce plan
                requestRepo.deleteByPlanId(plan.getId());
            }
        }

        repo.delete(op);
    }
}
