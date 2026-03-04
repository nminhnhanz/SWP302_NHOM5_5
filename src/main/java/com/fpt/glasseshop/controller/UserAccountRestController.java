package com.fpt.glasseshop.controller;

import com.fpt.glasseshop.entity.dto.*;
import com.fpt.glasseshop.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "UserAPI", description = "Operations related to user accounts")
public class UserAccountRestController {

    private final UserAccountService userAccountService;

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves a list of all user accounts (Admin only)")
    public ResponseEntity<ApiResponse<List<UserAccountDTO>>> getAllUsers() {
        List<UserAccountDTO> users = userAccountService.getAllUsersDTO();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves details of a specific user by their ID")
    public ResponseEntity<ApiResponse<UserAccountDTO>> getUserById(
            @Parameter(description = "ID of the user to retrieve", example = "5") @PathVariable Long id) {
        return userAccountService.getUserDTOById(id)
                .map(user -> ResponseEntity.ok(ApiResponse.success(user)))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("User not found")));
    }

    @PostMapping
    @Operation(summary = "Create a new user", description = "Adds a new user account to the system")
    public ResponseEntity<ApiResponse<UserAccountDTO>> createUser(@RequestBody UserAccountDTO userDTO) {
        UserAccountDTO created = userAccountService.createUser(userDTO);
        return ResponseEntity.status(201).body(ApiResponse.success("User created successfully", created));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user", description = "Removes a user account from the system")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(description = "ID of the user to delete", example = "5") @PathVariable Long id) {
        try {
            userAccountService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }
    // Chỉ cần login là dùng được
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponseDTO>> me(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(ApiResponse.success(userAccountService.getUser(email)));
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest req
    ) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(ApiResponse.success("Profile updated", userAccountService.updateMyProfile(email, req)));
    }

    @PutMapping("/email")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateEmail(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateEmailRequest req
    ) {
        String email = userDetails.getUsername();
        UserResponseDTO updated = userAccountService.updateMyEmail(email, req);

        // NOTE: Vì email là username, đổi email xong nên bắt user login lại để session update rõ ràng.
        return ResponseEntity.ok(ApiResponse.success("Email updated. Please login again.", updated));
    }

    @PutMapping("/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest req
    ) {
        String email = userDetails.getUsername();
        userAccountService.changeMyPassword(email, req);
        return ResponseEntity.ok(ApiResponse.success("Password updated. Please login again.", null));
    }
}
