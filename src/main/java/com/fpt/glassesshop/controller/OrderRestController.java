package com.fpt.glassesshop.controller;

import com.fpt.glassesshop.entity.dto.ApiResponse;
import com.fpt.glassesshop.entity.dto.OrderDTO;
import com.fpt.glassesshop.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "OrderAPI", description = "Operations related to orders")
public class OrderRestController {

    private final OrderService orderService;

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
