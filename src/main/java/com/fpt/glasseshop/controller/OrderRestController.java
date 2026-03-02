package com.fpt.glasseshop.controller;

import com.fpt.glasseshop.entity.UserAccount;
import com.fpt.glasseshop.entity.dto.ApiResponse;
import com.fpt.glasseshop.entity.dto.CreateOrderRequest;
import com.fpt.glasseshop.entity.dto.OrderDTO;
import com.fpt.glasseshop.repository.UserAccountRepository;
import com.fpt.glasseshop.service.OrderService;
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
            throw new IllegalArgumentException("User is not authenticated");
        }
        return userAccountRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));
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
            @Parameter(description = "ID of the order to retrieve", example = "101") @PathVariable Long id) {
        return orderService.getOrderDTOById(id)
                .map(order -> ResponseEntity.ok(ApiResponse.success(order)))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("Order not found")));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get orders by User ID", description = "Retrieves a list of orders placed by a specific user")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getOrdersByUserId(
            @Parameter(description = "ID of the user", example = "5") @PathVariable Long userId) {
        List<OrderDTO> orders = orderService.getOrdersDTOByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @PostMapping
    @Operation(summary = "Create a new order", description = "Adds a new order to the system")
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(@RequestBody OrderDTO orderDTO) {
        OrderDTO created = orderService.createOrder(orderDTO);
        return ResponseEntity.status(201).body(ApiResponse.success("Order created successfully", created));
    }

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

//    @PatchMapping("/{id}/recompute-status")
//    @PreAuthorize("hasAnyRole('OPERATIONAL_STAFF', 'ADMIN')")
//    public ResponseEntity<ApiResponse<OrderDTO>> recompute(@PathVariable Long id) {
//        // gọi service recompute rồi convertToDTO
//        // Bạn có thể inject OrderWorkflowService vào OrderService để gọi.
//        return ResponseEntity.status(501).body(ApiResponse.error("Not implemented yet"));
//    }
}
