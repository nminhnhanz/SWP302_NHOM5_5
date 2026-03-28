package com.fpt.glasseshop.controller;

import com.fpt.glasseshop.entity.Order;
import com.fpt.glasseshop.entity.ReturnRequest;
import com.fpt.glasseshop.entity.UserAccount;
import com.fpt.glasseshop.entity.dto.ApiResponse;
import com.fpt.glasseshop.entity.dto.ReturnRequestDTO;
import com.fpt.glasseshop.entity.dto.ReturnRequestResponseDTO;
import com.fpt.glasseshop.entity.dto.UpdateReturnStatusDTO;
import com.fpt.glasseshop.repository.UserAccountRepository;
import com.fpt.glasseshop.service.ReturnRequestService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/return-requests")
public class ReturnRequestController {

    private final ReturnRequestService returnRequestService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReturnRequestResponseDTO>> createReturnRequest(
            @RequestBody ReturnRequestDTO dto) throws BadRequestException {

        ReturnRequestResponseDTO saved = returnRequestService.createReturnRequest(dto);
        return ResponseEntity.ok(ApiResponse.success("Return request created successfully", saved));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReturnRequestResponseDTO>>> getAllReturnRequests() {
        List<ReturnRequestResponseDTO> list = returnRequestService.getAll();
        return ResponseEntity.ok(ApiResponse.success("Return requests fetched successfully", list));
    }
    //ADMIN UPDATE
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ReturnRequestResponseDTO>> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateReturnStatusDTO dto) {

        ReturnRequestResponseDTO responseDto = returnRequestService.updateStatus(id, dto.getStatus());

        return ResponseEntity.ok(ApiResponse.success("Return request status updated successfully", responseDto));
    }
}
