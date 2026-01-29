package com.fpt.glassesshop.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccountDTO {
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String accountStatus;
    private LocalDateTime createdAt;
}
