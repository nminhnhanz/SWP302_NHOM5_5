package com.fpt.glasseshop.entity.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VariantRequest {
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be at least 0")
    private Integer stockQuantity;
    @NotNull(message = "Frame size is required")
    private String frameSize;
    @NotNull(message = "Color is required")
    private String color;
    @NotNull(message = "Material is required")
    private String material;
    private String imageUrl;
    @NotNull(message = "Status is required")
    private String status;
    private Boolean active;
}
