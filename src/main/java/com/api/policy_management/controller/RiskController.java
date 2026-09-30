package com.api.policy_management.controller;

import com.api.policy_management.dto.RiskResponse;
import com.api.policy_management.service.PolicyService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/riesgos")
public class RiskController {

    private final PolicyService policyService;

    public RiskController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping("/{id}/cancelar")
    public RiskResponse cancel(@PathVariable Long id) {
        return policyService.cancelRisk(id);
    }
}
