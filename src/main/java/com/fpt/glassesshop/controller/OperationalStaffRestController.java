package com.fpt.glassesshop.controller;

import com.fpt.glassesshop.entity.dto.ApiResponse;
import com.fpt.glassesshop.entity.dto.RejectPrescriptionRequest;
import com.fpt.glassesshop.entity.dto.UpdatePreOrderStatusRequest;
import com.fpt.glassesshop.service.PreOrderWorkflowService;
import com.fpt.glassesshop.service.PrescriptionApprovalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/operational-staff")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('OPERATIONAL_STAFF', 'ADMIN')")
public class OperationalStaffRestController {

    private final PrescriptionApprovalService prescriptionApprovalService;
    private final PreOrderWorkflowService preOrderWorkflowService;

    @PostMapping("/order-items/{orderItemId}/prescription/approve")
    public ResponseEntity<ApiResponse<Void>> approvePrescription(@PathVariable Long orderItemId) {
        prescriptionApprovalService.approve(orderItemId);
        return ResponseEntity.ok(ApiResponse.success("Prescription approved", null));
    }

    @PostMapping("/order-items/{orderItemId}/prescription/reject")
    public ResponseEntity<ApiResponse<Void>> rejectPrescription(
            @PathVariable Long orderItemId,
            @Valid @RequestBody RejectPrescriptionRequest req
    ) {
        prescriptionApprovalService.reject(orderItemId, req.reason());
        return ResponseEntity.ok(ApiResponse.success("Prescription rejected", null));
    }

    @PatchMapping("/order-items/{orderItemId}/preorder/status")
    public ResponseEntity<ApiResponse<Void>> updatePreOrderStatus(
            @PathVariable Long orderItemId,
            @Valid @RequestBody UpdatePreOrderStatusRequest req
    ) {
        preOrderWorkflowService.updateStatus(orderItemId, req.status());
        return ResponseEntity.ok(ApiResponse.success("PreOrder status updated", null));
    }
}