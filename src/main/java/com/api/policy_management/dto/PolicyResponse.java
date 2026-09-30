package com.api.policy_management.dto;

import com.api.policy_management.model.Policy;
import com.api.policy_management.model.PolicyStatus;
import com.api.policy_management.model.PolicyType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PolicyResponse(
        Long id,
        PolicyType tipo,
        PolicyStatus estado,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Integer mesesVigencia,
        BigDecimal canonMensual,
        BigDecimal prima
) {
    public static PolicyResponse from(Policy policy) {
        return new PolicyResponse(
                policy.getId(),
                policy.getType(),
                policy.getStatus(),
                policy.getStartDate(),
                policy.getEndDate(),
                policy.getCoverageMonths(),
                policy.getMonthlyRent(),
                policy.getPremium()
        );
    }
}
