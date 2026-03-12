package com.fpt.glasseshop.controller;

import com.fpt.glasseshop.entity.dto.ApiResponse;
import com.fpt.glasseshop.entity.dto.UserAccountDTO;
import com.fpt.glasseshop.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    @GetMapping("/check-email")
    @Operation(summary = "Check if email exists", description = "Return true or false")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(ApiResponse.success(
                userAccountService.checkEmailExists(email)));
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
}
