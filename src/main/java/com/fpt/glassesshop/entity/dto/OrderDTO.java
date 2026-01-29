package com.fpt.glassesshop.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private Long orderId;
    private Long userId;
    private String userName;
    private String userEmail;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalPrice;
    private AddressDTO shippingAddress;
    private AddressDTO billingAddress;
    private String paymentStatus;
    private List<OrderItemDTO> orderItems;
    private Integer totalItems;
}
