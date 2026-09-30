package com.api.policy_management.dto;

import com.api.policy_management.model.Risk;
import com.api.policy_management.model.RiskStatus;

public record RiskResponse(
        Long id,
        Long polizaId,
        String direccionInmueble,
        String asegurado,
        String beneficiario,
        RiskStatus estado
) {
    public static RiskResponse from(Risk risk) {
        return new RiskResponse(
                risk.getId(),
                risk.getPolicy().getId(),
                risk.getPropertyAddress(),
                risk.getInsuredName(),
                risk.getBeneficiaryName(),
                risk.getStatus()
        );
    }
}
