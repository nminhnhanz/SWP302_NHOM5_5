package com.fpt.glasseshop.controller;

import com.fpt.glasseshop.entity.Payment;
import com.fpt.glasseshop.entity.dto.ApiResponse;
import com.fpt.glasseshop.entity.dto.PaymentDTO;
import com.fpt.glasseshop.entity.dto.PaymentRequest;
import com.fpt.glasseshop.service.PaymentService;
import com.fpt.glasseshop.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "PaymentAPI", description = "Operations related to payment processing")
public class PaymentRestController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    @Operation(summary = "Process order payment", description = "Simulates payment processing and updates order status")
    public ResponseEntity<ApiResponse<PaymentDTO>> processPayment(@Valid @RequestBody PaymentRequest request) {
        try {
            Payment payment = paymentService.processPayment(request);
            PaymentDTO response = PaymentDTO.builder()
                    .paymentId(payment.getPaymentId())
                    .orderId(payment.getOrder().getOrderId())
                    .paymentMethod(payment.getPaymentMethod())
                    .status(payment.getStatus())
                    .amount(payment.getAmount())
                    .paidAt(payment.getPaidAt())
                    .build();
            if ("SUCCESS".equals(payment.getStatus())) {
                return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", response));
            } else {
                return ResponseEntity.status(400).body(ApiResponse.error("Payment failed", response));
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Unexpected error: " + e.getMessage()));
        }
    }
}
