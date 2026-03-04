package com.fpt.glasseshop.entity.dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String accountStatus;
}
