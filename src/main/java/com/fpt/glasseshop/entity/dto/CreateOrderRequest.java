package com.fpt.glasseshop.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {
    private String fullName;
    private String phone;
    private String address;
    private String note;
    private String paymentMethod;
    private java.math.BigDecimal shippingFee;
    private java.math.BigDecimal voucherDiscount;
    private String idempotencyKey;
}
