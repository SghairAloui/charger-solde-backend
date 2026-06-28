package com.chargersolde.repository;

import com.chargersolde.entity.RechargePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RechargePlanRepository extends JpaRepository<RechargePlan, Long> {
    List<RechargePlan> findByOperatorId(Long operatorId);

    List<RechargePlan> findByOperatorIdAndActiveTrue(Long operatorId);
}
