package com.fpt.glassesshop.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object representing an item in an order")
public class OrderItemDTO {
    @Schema(description = "Unique identifier of the order item", example = "501")
    private Long orderItemId;

    @Schema(description = "ID of the product variant", example = "10")
    private Long variantId;

    @Schema(description = "Name of the product", example = "Ray-Ban Aviator")
    private String productName;

    @Schema(description = "Color of the product variant", example = "Gold")
    private String variantColor;

    @Schema(description = "Size of the frame", example = "Medium")
    private String variantSize;

    @Schema(description = "URL of the product image")
    private String imageUrl;

    @Schema(description = "Quantity ordered", example = "1")
    private Integer quantity;

    @Schema(description = "Price per unit", example = "150.00")
    private BigDecimal unitPrice;

    @Schema(description = "Subtotal for this item (quantity * unitPrice)", example = "150.00")
    private BigDecimal subtotal;

    @Schema(description = "Fulfillment type (e.g., SHIP_TO_HOME, PICKUP)", example = "SHIP_TO_HOME")
    private String fulfillmentType;

    @Schema(description = "Type of item (PRESCRIPTION, PRE_ORDER, IN_STOCK)", example = "IN_STOCK")
    private String itemType;

    @Schema(description = "Prescription details if applicable")
    private PrescriptionDTO prescription;
}
