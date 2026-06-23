package com.chargersolde.repository;

import com.chargersolde.entity.RechargeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RechargeRequestRepository extends JpaRepository<RechargeRequest, Long> {
    List<RechargeRequest> findByClientId(Long clientId);

    long count();


    long countByCreatedAtBetween(
            LocalDateTime start,
            LocalDateTime end
    );

}