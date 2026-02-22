package com.fpt.glassesshop.entity.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePreOrderStatusRequest(
        @NotBlank String status
) {}