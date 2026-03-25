package com.fpt.glasseshop.controller;

import com.fpt.glasseshop.entity.ReturnRequest;
import com.fpt.glasseshop.entity.dto.ApiResponse;
import com.fpt.glasseshop.entity.dto.ReturnRequestDTO;
import com.fpt.glasseshop.entity.dto.ReturnRequestResponseDTO;
import com.fpt.glasseshop.entity.dto.UpdateReturnStatusDTO;
import com.fpt.glasseshop.service.ReturnRequestService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    //ADMIN UPDATE
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ReturnRequestResponseDTO>> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateReturnStatusDTO dto) {

        ReturnRequest updated = returnRequestService.updateStatus(id, dto.getStatus());

        ReturnRequestResponseDTO responseDto = returnRequestService.mapToDTO(updated);

        return ResponseEntity.ok(ApiResponse.success("Return request status updated successfully", responseDto));
    }
}
