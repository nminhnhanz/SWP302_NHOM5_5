package com.fpt.glassesshop.model.dto;
import lombok.*;

import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserAccountDto {

    private Long userId;

    private String name;

    private String email;

    private String phone;

    private String role;

    private String accountStatus;

    private LocalDateTime createdAt;
}
