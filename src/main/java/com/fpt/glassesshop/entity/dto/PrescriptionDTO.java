package com.fpt.glassesshop.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionDTO {
    private Long prescriptionId;
    private Long orderItemId;

    @NotNull(message = "Left eye SPH is required")
    private BigDecimal sphLeft;

    @NotNull(message = "Right eye SPH is required")
    private BigDecimal sphRight;

    private BigDecimal cylLeft;
    private BigDecimal cylRight;
    private Integer axisLeft;
    private Integer axisRight;

    @NotNull(message = "PD (Pupillary Distance) is required")
    private BigDecimal pd;

    private String doctorName;
    private LocalDate expirationDate;
    private String status;
    private LocalDateTime createdAt;
}
