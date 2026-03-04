package com.fpt.glasseshop.entity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmailRequest {
    @Email
    @NotBlank
    private String newEmail;

    @NotBlank
    private String password; // confirm password để đổi email
}
