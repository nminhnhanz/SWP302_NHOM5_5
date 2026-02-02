package com.fpt.glassesshop.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object representing an order")
public class OrderDTO {
    @Schema(description = "Unique identifier of the order", example = "101")
    private Long orderId;

    @Schema(description = "ID of the user who placed the order", example = "5")
    private Long userId;

    @Schema(description = "Name of the user who placed the order", example = "John Doe")
    private String userName;

    @Schema(description = "Email of the user who placed the order", example = "john.doe@example.com")
    private String userEmail;

    @Schema(description = "Timestamp when the order was placed")
    private LocalDateTime orderDate;

    @Schema(description = "Current status of the order", example = "PENDING")
    private String status;

    @Schema(description = "Total price of the order", example = "250.00")
    private BigDecimal totalPrice;

    @Schema(description = "Shipping address details")
    private AddressDTO shippingAddress;

    @Schema(description = "Billing address details")
    private AddressDTO billingAddress;

    @Schema(description = "Payment status of the order", example = "PAID")
    private String paymentStatus;

    @Schema(description = "List of items in the order")
    private List<OrderItemDTO> orderItems;

    @Schema(description = "Total number of items in the order", example = "3")
    private Integer totalItems;
}
