package com.chargersolde.controller;

import com.chargersolde.dto.AdminDashboardDTO;
import com.chargersolde.dto.ClientRechargeSummary;
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
    import java.util.List;
import com.chargersolde.entity.RechargeStatus;

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

    @PutMapping("/plans/{id}")
    public RechargePlan updatePlan(@PathVariable Long id, @RequestBody RechargePlanDTO dto) {
        return planService.update(id, dto);
    }

    @DeleteMapping("/plans/{id}")
    public void deletePlan(@PathVariable Long id) {
        planService.delete(id);
    }

    @DeleteMapping("/operators/{id}")
    public void deleteOperator(@PathVariable Long id) {
        operatorService.delete(id);
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

    @GetMapping("/clients/recharge-summary")
    public List<ClientRechargeSummary> getClientRechargeSummary() {
        return service.getClientRechargeSummary();
    }

    @GetMapping("/recharge")
    public ResponseEntity<?> getRecharges(
            @RequestParam(required = false) RechargeStatus status,
            @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(
                rechargeService.getAllRequests(status, page)
        );
    }

    @PatchMapping("/plans/{id}/block")
    public RechargePlan block(@PathVariable Long id) {
        return planService.setActive(id, false);
    }

    @PatchMapping("/plans/{id}/unblock")
    public RechargePlan unblock(@PathVariable Long id) {
        return planService.setActive(id, true);
    }

}