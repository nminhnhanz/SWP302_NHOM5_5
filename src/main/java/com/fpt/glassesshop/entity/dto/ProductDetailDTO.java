package com.fpt.glassesshop.entity.dto;

import com.fpt.glassesshop.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailDTO {
    private Long productId;
    private Product.ProductType productType;
    private String name;
    private String brand;
    private String description;
    private boolean isPrescriptionSupported;
    private LocalDateTime createdAt;
    private List<ProductVariantDTO> variants;
    private List<ReviewDTO> reviews;
    private Double averageRating;
}
