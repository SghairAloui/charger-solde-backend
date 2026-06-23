package com.chargersolde.controller;

import com.chargersolde.dto.CreateRechargeRequestDTO;
import com.chargersolde.entity.RechargeRequest;
import com.chargersolde.service.RechargeService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/recharge")
    public RechargeRequest create(
            @RequestBody CreateRechargeRequestDTO dto,
            Authentication auth) {

        return rechargeService.createRequest(dto, auth.getName());
    }

    @GetMapping("/recharges")
    public List<RechargeRequest> myRequests(Authentication auth) {
        return rechargeService.getMyRequests(auth.getName());
    }
}