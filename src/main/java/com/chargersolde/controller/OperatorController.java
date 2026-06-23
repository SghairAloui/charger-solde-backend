package com.chargersolde.controller;

import com.chargersolde.entity.Operator;
import com.chargersolde.entity.RechargePlan;
import com.chargersolde.service.OperatorService;
import com.chargersolde.service.RechargePlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/operators")
@RequiredArgsConstructor
public class OperatorController {

    private final OperatorService service;
    private final RechargePlanService planService;

    @GetMapping
    public List<Operator> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}/plans")
    public List<RechargePlan> getPlans(@PathVariable Long id) {
        return planService.getByOperator(id);
    }
}
