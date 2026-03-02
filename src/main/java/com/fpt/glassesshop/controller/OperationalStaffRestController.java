//package com.fpt.glassesshop.controller;
//
//import com.fpt.glassesshop.entity.dto.ApiResponse;
//import com.fpt.glassesshop.entity.dto.RejectPrescriptionRequest;
//import com.fpt.glassesshop.entity.dto.UpdatePreOrderStatusRequest;
//import com.fpt.glassesshop.service.PreOrderWorkflowService;
//import com.fpt.glassesshop.service.PrescriptionApprovalService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/operational-staff")
//@RequiredArgsConstructor
//@PreAuthorize("hasAnyRole('OPERATIONAL_STAFF', 'ADMIN')")
//public class OperationalStaffRestController {
//
//    private final PrescriptionApprovalService prescriptionApprovalService;
//    private final PreOrderWorkflowService preOrderWorkflowService;
//
//    @PostMapping("/order-items/{orderItemId}/prescription/approve")
//    public ResponseEntity<ApiResponse<Void>> approvePrescription(@PathVariable Long orderItemId) {
//        prescriptionApprovalService.approve(orderItemId);
//        return ResponseEntity.ok(ApiResponse.success("Prescription approved", null));
//    }
//
//    @PostMapping("/order-items/{orderItemId}/prescription/reject")
//    public ResponseEntity<ApiResponse<Void>> rejectPrescription(
//            @PathVariable Long orderItemId,
//            @Valid @RequestBody RejectPrescriptionRequest req
//    ) {
//        prescriptionApprovalService.reject(orderItemId, req.getReason());
//        return ResponseEntity.ok(ApiResponse.success("Prescription rejected", null));
//    }
//
//    @PatchMapping("/order-items/{orderItemId}/preorder/status")
//    public ResponseEntity<ApiResponse<Void>> updatePreOrderStatus(
//            @PathVariable Long orderItemId,
//            @Valid @RequestBody UpdatePreOrderStatusRequest req
//    ) {
//        preOrderWorkflowService.updateStatus(orderItemId, String.valueOf(req.getStatus()));
//        return ResponseEntity.ok(ApiResponse.success("PreOrder status updated", null));
//    }
//}
package com.fpt.glassesshop.controller;

import com.fpt.glassesshop.entity.OrderItem;
import com.fpt.glassesshop.entity.dto.*;
import com.fpt.glassesshop.service.OrderItemService;
import com.fpt.glassesshop.service.PreOrderWorkflowService;
import com.fpt.glassesshop.service.PrescriptionApprovalService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/operational-staff")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('OPERATIONAL_STAFF', 'ADMIN')")
@Tag(name = "OperationalStaffAPI")
public class OperationalStaffRestController {

    private final OrderItemService orderItemService;
    private final PrescriptionApprovalService prescriptionApprovalService;
    private final PreOrderWorkflowService preOrderWorkflowService;

    // ===== Dashboard: FE StaffDashboardPage.jsx sẽ gọi endpoint này =====
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> dashboard() {
        long presPending = orderItemService.countPendingPrescriptionItems();
        long preWaiting = orderItemService.countWaitingStockPreOrderItems();
        long inStock = orderItemService.countInStockItems();

        List<OrderItemDTO> prescriptionItems = orderItemService.getPendingPrescriptionItems()
                .stream().map(this::toItemDTO).collect(Collectors.toList());

        List<OrderItemDTO> preOrderItems = orderItemService.getWaitingStockPreOrderItems()
                .stream().map(this::toItemDTO).collect(Collectors.toList());

        List<OrderItemDTO> inStockItems = orderItemService.getInStockItems()
                .stream().map(this::toItemDTO).collect(Collectors.toList());

        Map<String, Object> stats = new HashMap<>();
        stats.put("prescriptionPending", presPending);
        stats.put("preOrderWaiting", preWaiting);
        stats.put("inStock", inStock);

        Map<String, Object> data = new HashMap<>();
        data.put("stats", stats);
        data.put("prescriptionItems", prescriptionItems);
        data.put("preOrderItems", preOrderItems);
        data.put("inStockItems", inStockItems);

        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // ===== Prescription approval =====
    @PostMapping("/order-items/{orderItemId}/prescription/approve")
    public ResponseEntity<ApiResponse<Void>> approve(@PathVariable Long orderItemId) {
        prescriptionApprovalService.approve(orderItemId);
        return ResponseEntity.ok(ApiResponse.success("Prescription approved", null));
    }

    @PostMapping("/order-items/{orderItemId}/prescription/reject")
    public ResponseEntity<ApiResponse<Void>> reject(
            @PathVariable Long orderItemId,
            @Valid @RequestBody RejectPrescriptionRequest req
    ) {
        prescriptionApprovalService.reject(orderItemId, req.getReason());
        return ResponseEntity.ok(ApiResponse.success("Prescription rejected", null));
    }

    // ===== PreOrder update =====
    @PatchMapping("/order-items/{orderItemId}/preorder/status")
    public ResponseEntity<ApiResponse<Void>> updatePreOrder(
            @PathVariable Long orderItemId,
            @Valid @RequestBody UpdatePreOrderStatusRequest req
    ) {
        preOrderWorkflowService.updateStatus(orderItemId, req.getStatus());
        return ResponseEntity.ok(ApiResponse.success("PreOrder status updated", null));
    }

    private OrderItemDTO toItemDTO(OrderItem item) {
        // giống cách mapping trong OrderService của bạn, nhưng không cần subtotal nếu không muốn
        return OrderItemDTO.builder()
                .orderItemId(item.getOrderItemId())
                .variantId(item.getVariant() != null ? item.getVariant().getVariantId() : null)
                .productName(item.getVariant() != null && item.getVariant().getProduct() != null
                        ? item.getVariant().getProduct().getName()
                        : null)
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getUnitPrice() != null && item.getQuantity() != null
                        ? item.getUnitPrice().multiply(new java.math.BigDecimal(item.getQuantity()))
                        : null)
                .fulfillmentType(item.getFulfillmentType())
                .build();
    }
}