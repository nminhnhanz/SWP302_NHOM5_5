package com.fpt.glasseshop.service;

import com.fpt.glasseshop.entity.Order;
import com.fpt.glasseshop.entity.ReturnRequest;
import com.fpt.glasseshop.entity.dto.ReturnRequestDTO;
import com.fpt.glasseshop.entity.dto.ReturnRequestResponseDTO;
import com.fpt.glasseshop.repository.OrderRepository;
import com.fpt.glasseshop.repository.ReturnRequestRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReturnRequestService {

    private final ReturnRequestRepository returnRequestRepo;
    private final OrderRepository orderRepository;

    public ReturnRequestResponseDTO createReturnRequest(ReturnRequestDTO dto) throws BadRequestException {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getStatus().equals("DELIVERED")) {
            throw new BadRequestException("Only delivered orders can be returned");
        }

        ReturnRequest request = ReturnRequest.builder()
                .order(order)
                .reason(dto.getReason())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .status(ReturnRequest.ReturnStatus.PENDING)
                .build();

        ReturnRequest saved = returnRequestRepo.save(request);

        return mapToDTO(saved);
    }


    public ReturnRequest updateStatus(Long id, ReturnRequest.ReturnStatus status) {

        ReturnRequest request = returnRequestRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Return request not found"));

        request.setStatus(status);

        // nếu đã xử lý xong thì set resolvedAt
        if (status == ReturnRequest.ReturnStatus.APPROVED
                || status == ReturnRequest.ReturnStatus.REJECTED
                || status == ReturnRequest.ReturnStatus.COMPLETED) {
            request.setResolvedAt(LocalDateTime.now());
        }

        return returnRequestRepo.save(request);
    }

    public ReturnRequestResponseDTO mapToDTO(ReturnRequest request) {
        return ReturnRequestResponseDTO.builder()
                .requestId(request.getRequestId())
                .orderId(request.getOrder().getOrderId())
                .reason(request.getReason())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .status(request.getStatus().name())
                .requestedAt(request.getRequestedAt())
                .resolvedAt(request.getResolvedAt())
                .build();
    }
}
