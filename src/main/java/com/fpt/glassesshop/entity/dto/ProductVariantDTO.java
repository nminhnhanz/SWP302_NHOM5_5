package com.fpt.glassesshop.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object for product variants")
public class ProductVariantDTO {
    @Schema(description = "Unique identifier of the variant", example = "10")
    private Long variantId;

    @Schema(description = "ID of the associated product", example = "1")
    private Long productId;

    @Schema(description = "Price of this specific variant", example = "150.00")
    private BigDecimal price;

    @Schema(description = "Quantity available in stock", example = "100")
    private Integer stockQuantity;

    @Schema(description = "Size of the frame", example = "Large")
    private String frameSize;

    @Schema(description = "Color of the product", example = "Matte Black")
    private String color;

    @Schema(description = "Material used for the product", example = "Titanium")
    private String material;

    @Schema(description = "URL for the main image of this variant", example = "/images/products/aviator-black.jpg")
    private String imageUrl;

    @Schema(description = "Current status of the variant (e.g., AVAILABLE, OUT_OF_STOCK)", example = "AVAILABLE")
    private String status;
}
