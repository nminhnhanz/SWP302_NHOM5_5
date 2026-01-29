package com.fpt.glassesshop.entity.dto;

import com.fpt.glassesshop.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long productId;
    private Product.ProductType productType;
    private String name;
    private String brand;
    private String description;
    private boolean isPrescriptionSupported;
    private LocalDateTime createdAt;
}
