package com.api.policy_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CoreEventRequest(
        @NotBlank String evento,
        @NotNull Long polizaId
) {
}
