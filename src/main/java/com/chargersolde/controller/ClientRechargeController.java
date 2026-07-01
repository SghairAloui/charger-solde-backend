package com.chargersolde.controller;

import com.chargersolde.dto.CreateRechargeRequestDTO;
import com.chargersolde.entity.RechargeRequest;
import com.chargersolde.entity.SystemAlert;
import com.chargersolde.service.AlertService;
import com.chargersolde.service.RechargeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENT')")
public class ClientRechargeController {

    private final RechargeService rechargeService;
    private final AlertService alertService;
    @PostMapping("/recharge")
    public RechargeRequest create(
            @RequestBody CreateRechargeRequestDTO dto,
            Authentication auth) {

        return rechargeService.createRequest(dto, auth.getName());
    }

    @GetMapping("/recharges")
    public Page<RechargeRequest> myRequests(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return rechargeService.getMyRequests(auth.getName(), page, size);
    }
    @GetMapping("/recharges/all")
    public List<RechargeRequest> getAllMyRequests(Authentication auth) {
        return rechargeService.getMyRequests(auth.getName());
    }

    @GetMapping("/alerts")
    public List<SystemAlert> alerts(){

        return alertService.getActiveAlerts();

    }
}