package com.fpt.glassesshop.entity.dto;

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
    @NotNull(message = "Shipping address is required")
    private Long shippingAddressId;

    @NotNull(message = "Billing address is required")
    private Long billingAddressId;

    @NotNull(message = "Order items are required")
    private List<OrderItemRequest> items;

    private String paymentMethod;
}
