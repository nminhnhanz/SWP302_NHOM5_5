package com.fpt.glassesshop.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantDTO {
    private Long variantId;
    private Long productId;
    private BigDecimal price;
    private Integer stockQuantity;
    private String frameSize;
    private String color;
    private String material;
    private String imageUrl;
    private String status;
}
