package com.fpt.glassesshop.entity.dto;

import com.fpt.glassesshop.constant.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePreOrderStatusRequest {
    @NotBlank
    private String status;
    private String note;
}