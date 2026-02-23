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
public class LensOptionDTO {
    private Long lensOptionId;
    private String type;
    private String thickness;
    private String coating;
    private String color;
    private BigDecimal price;

    // Additional field for compatibility API
    private String compatibilityNote;
}
