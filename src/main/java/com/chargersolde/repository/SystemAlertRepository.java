package com.chargersolde.repository;

import com.chargersolde.entity.SystemAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemAlertRepository
        extends JpaRepository<SystemAlert,Long> {


    List<SystemAlert> findByActiveTrueOrderByCreatedAtDesc();


}