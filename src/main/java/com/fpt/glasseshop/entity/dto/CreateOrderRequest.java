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
@Schema(description = "Request object to create a new order")
public class CreateOrderRequest {
    @NotNull(message = "Shipping address is required")
    @Schema(description = "ID of the shipping address", example = "1")
    private Long shippingAddressId;

    @NotNull(message = "Billing address is required")
    @Schema(description = "ID of the billing address", example = "1")
    private Long billingAddressId;

    @NotNull(message = "Order items are required")
    private List<OrderItemRequest> items;

    @Schema(description = "Payment method", example = "COD")
    private String paymentMethod;
}
