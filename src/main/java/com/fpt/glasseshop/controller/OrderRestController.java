package com.fpt.glasseshop.controller;

import com.fpt.glasseshop.entity.UserAccount;
import com.fpt.glasseshop.entity.dto.ApiResponse;
import com.fpt.glasseshop.entity.dto.CreateOrderRequest;
import com.fpt.glasseshop.entity.dto.OrderDTO;
import com.fpt.glasseshop.repository.UserAccountRepository;
import com.fpt.glasseshop.service.OrderService;
import com.fpt.glasseshop.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "OrderAPI", description = "Operations related to orders")
public class OrderRestController {

    private final OrderService orderService;
    private final UserAccountRepository userAccountRepository;

    private UserAccount getAuthenticatedUser(Principal principal) {
        if (principal == null) {
            throw new org.springframework.security.access.AccessDeniedException("User is not authenticated");
        }
        return userAccountRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException(
                        "Authenticated user not found"));
    }

    private void checkIfOwnerOrStaff(Long userId, Principal principal) {
        UserAccount currentUser = getAuthenticatedUser(principal);
        boolean isStaffOrAdmin = "ADMIN".equals(currentUser.getRole())
                || "OPERATIONAL_STAFF".equals(currentUser.getRole());
        if (!userId.equals(currentUser.getUserId()) && !isStaffOrAdmin) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You are not authorized to access this information");
        }
    }

    @GetMapping
    @Operation(summary = "Get all orders", description = "Retrieves a list of all orders (Admin/Staff only)")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrdersDTO();
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Retrieves details of a specific order by its unique identifier")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(
            Principal principal,
            @Parameter(description = "ID of the order to retrieve", example = "101") @PathVariable Long id) {
        return orderService.getOrderDTOById(id)
                .map(order -> {
                    checkIfOwnerOrStaff(order.getUserId(), principal);
                    return ResponseEntity.ok(ApiResponse.success(order));
                })
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("Order not found")));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get orders by User ID", description = "Retrieves a list of orders placed by a specific user")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getOrdersByUserId(
            Principal principal,
            @Parameter(description = "ID of the user", example = "5") @PathVariable Long userId) {
        checkIfOwnerOrStaff(userId, principal);
        List<OrderDTO> orders = orderService.getOrdersDTOByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    // Users should use /checkout for order creation from cart
    @PostMapping("/checkout")
    @Operation(summary = "Checkout cart to create order", description = "Creates an order from the user's current shopping cart")
    public ResponseEntity<ApiResponse<OrderDTO>> checkout(
            Principal principal,
            @Valid @RequestBody CreateOrderRequest request) {
        try {
            UserAccount user = getAuthenticatedUser(principal);
            OrderDTO orderDTO = orderService.createOrderFromCart(user, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Order completed successfully", orderDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error during checkout: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status", description = "Updates the status of an existing order")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrderStatus(
            Principal principal,
            @Parameter(description = "ID of the order", example = "101") @PathVariable Long id,
            @Parameter(description = "New status (WAITING_FOR_CONFIRMATION, DELIVERING, DELIVERED, CANCELED)", example = "DELIVERING") @RequestParam String status) {
        try {
            OrderDTO updatedOrder = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("Order status updated", updatedOrder));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order", description = "Removes an order by its unique identifier")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(
            @Parameter(description = "ID of the order to delete", example = "101") @PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok(ApiResponse.success("Order deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }
}
