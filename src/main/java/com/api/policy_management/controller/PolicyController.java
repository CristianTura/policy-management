package com.api.policy_management.controller;

import com.api.policy_management.dto.CreateRiskRequest;
import com.api.policy_management.dto.PolicyResponse;
import com.api.policy_management.dto.RiskResponse;
import com.api.policy_management.model.PolicyStatus;
import com.api.policy_management.model.PolicyType;
import com.api.policy_management.service.PolicyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/polizas")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @GetMapping
    public List<PolicyResponse> list(
            @RequestParam(required = false) PolicyType tipo,
            @RequestParam(required = false) PolicyStatus estado
    ) {
        return policyService.list(tipo, estado);
    }

    @GetMapping("/{id}/riesgos")
    public List<RiskResponse> listRisks(@PathVariable Long id) {
        return policyService.listRisks(id);
    }

    @PostMapping("/{id}/renovar")
    public PolicyResponse renew(@PathVariable Long id) {
        return policyService.renew(id);
    }

    @PostMapping("/{id}/cancelar")
    public PolicyResponse cancel(@PathVariable Long id) {
        return policyService.cancel(id);
    }

    @PostMapping("/{id}/riesgos")
    @ResponseStatus(HttpStatus.CREATED)
    public RiskResponse addRisk(@PathVariable Long id, @Valid @RequestBody CreateRiskRequest request) {
        return policyService.addRisk(id, request);
    }
}
