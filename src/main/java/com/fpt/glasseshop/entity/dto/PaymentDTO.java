package com.fpt.glasseshop.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PaymentDTO {
    private Long paymentId;
    private Long orderId;
    private String paymentMethod;
    private BigDecimal amount;
    private String status;
    private LocalDateTime paidAt;
}
