package com.chargersolde.repository;

import com.chargersolde.entity.RechargeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

import com.chargersolde.entity.RechargeStatus;

@Repository
public interface RechargeRequestRepository extends JpaRepository<RechargeRequest, Long> {
    List<RechargeRequest> findByClientId(Long clientId);
    List<RechargeRequest> findByStatus(RechargeStatus status);

    long count();

    long countByCreatedAtBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    long countByClientId(Long clientId);
    long countByClientIdAndStatus(Long clientId, RechargeStatus status);

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM RechargeRequest r WHERE r.client.id = :clientId")
    double sumAmountByClientId(Long clientId);

    @Modifying
    @Transactional
    @Query("UPDATE RechargeRequest r SET r.plan = NULL WHERE r.plan.id = :planId")
    void nullifyPlanReferences(Long planId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RechargeRequest r WHERE r.plan.id = :planId")
    void deleteByPlanId(Long planId);

    Page<RechargeRequest> findAll(Pageable pageable);

    Page<RechargeRequest> findByStatus(RechargeStatus status, Pageable pageable);

    Page<RechargeRequest> findByClientId(Long clientId, Pageable pageable);
    // 👇 ICI TU AJOUTES LA MÉTHODE 24H
    List<RechargeRequest> findByCreatedAtBeforeAndStatus(
            LocalDateTime date,
            RechargeStatus status
    );
}