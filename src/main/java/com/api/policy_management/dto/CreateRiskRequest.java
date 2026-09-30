package com.api.policy_management.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateRiskRequest(
        @NotBlank String direccionInmueble,
        @NotBlank String asegurado,
        @NotBlank String beneficiario
) {
}
