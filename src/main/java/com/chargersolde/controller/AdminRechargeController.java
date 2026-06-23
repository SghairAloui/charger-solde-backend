package com.chargersolde.controller;

import com.chargersolde.dto.AdminDashboardDTO;
import com.chargersolde.dto.RechargePlanDTO;
import com.chargersolde.entity.Operator;
import com.chargersolde.entity.RechargePlan;
import com.chargersolde.entity.RechargeRequest;
import com.chargersolde.service.DashboardService;
import com.chargersolde.service.OperatorService;
import com.chargersolde.service.RechargePlanService;
import com.chargersolde.service.RechargeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminRechargeController {

    private final OperatorService operatorService;
    private final RechargePlanService planService;
    private final RechargeService rechargeService;

    private final DashboardService service;


    @PostMapping("/operators")
    public Operator createOperator(@RequestBody Operator op) {
        return operatorService.create(op);
    }

    @PostMapping("/plans")
    public RechargePlan createPlan(@RequestBody RechargePlanDTO dto) {
        return planService.create(dto);
    }

    @PatchMapping("/recharge/{id}")
    public RechargeRequest validate(
            @PathVariable Long id,
            @RequestParam boolean accept) {
        return rechargeService.validate(id, accept);
    }

    @GetMapping
    public ResponseEntity<AdminDashboardDTO> dashboard(){


        return ResponseEntity.ok(
                service.getDashboard()
        );


    }

}