package com.fpt.glasseshop.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnRequestResponseDTO {
    private Long requestId;
    private Long orderId;
    private Long orderItemId;
    private String reason;
    private String description;
    private String imageUrl;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime resolvedAt;
}