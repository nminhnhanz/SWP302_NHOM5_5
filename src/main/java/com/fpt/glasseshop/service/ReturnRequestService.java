package com.fpt.glasseshop.service;

import com.fpt.glasseshop.entity.Order;
import com.fpt.glasseshop.entity.OrderItem;
import com.fpt.glasseshop.entity.ReturnRequest;
import com.fpt.glasseshop.entity.UserAccount;
import com.fpt.glasseshop.entity.dto.ReturnRequestDTO;
import com.fpt.glasseshop.entity.dto.ReturnRequestResponseDTO;
import com.fpt.glasseshop.entity.dto.UpdateReturnStatusDTO;
import com.fpt.glasseshop.repository.OrderItemRepository;
import com.fpt.glasseshop.repository.OrderRepository;
import com.fpt.glasseshop.repository.ReturnRequestRepository;
import com.fpt.glasseshop.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReturnRequestService {

    private final ReturnRequestRepository returnRequestRepo;
    private final OrderRepository orderRepository;
    private final UserAccountRepository userAccountRepository;
    private final OrderItemRepository orderItemRepository;

    private UserAccount getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        System.out.println("EMAIL FROM SECURITY CONTEXT = " + email);
        System.out.println("TOKEN ROLE = " + role);
        if (email == null) {
            throw new AccessDeniedException("User is not authenticated");
        }

        return userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("User not found"));
    }

    public ReturnRequestResponseDTO createReturnRequest(ReturnRequestDTO dto) throws BadRequestException {

        UserAccount currentUser = getCurrentUser();

        OrderItem orderItem = orderItemRepository.findById(dto.getOrderItemId())
                .orElseThrow(() -> new RuntimeException("Order not found"));
        Order order = orderItem.getOrder();
        order.setPaymentStatus("PAID");
        // 1. check owner
        if (order.getUser() == null || !order.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new BadRequestException("You are not allowed to return this order item");
        }

        // 2. đơn delivered mới được đổi/trả
        if (!"DELIVERED".equalsIgnoreCase(order.getStatus())) {
            throw new BadRequestException("Only delivered orders can be returned");
        }

        // 3. đơn đã thanh toán mới được đổi/trả
        if (!"PAID".equalsIgnoreCase(order.getPaymentStatus())) {
            throw new BadRequestException("Only paid orders can be returned");
        }

        // 4. check trùng theo orderItem
        if (returnRequestRepo.existsByOrderItemOrderItemId(orderItem.getOrderItemId())) {
            throw new BadRequestException("Return request already exists for this order item");
        }

        // 5. check time 7 day
        if (order.getDeliveredAt() != null &&
                order.getDeliveredAt().plusDays(7).isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Return period expired");
        }

        // 6. validate reason
        if (dto.getReason() == null || dto.getReason().trim().isEmpty()) {
            throw new BadRequestException("Reason is required");
        }

        ReturnRequest request = ReturnRequest.builder()
                .orderItem(orderItem)
                .reason(dto.getReason())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .status(ReturnRequest.ReturnStatus.PENDING)
                .build();

        ReturnRequest saved = returnRequestRepo.save(request);

        return mapToDTO(saved);
    }

    public List<ReturnRequestResponseDTO> getAll() {
        return returnRequestRepo.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional
    public ReturnRequestResponseDTO updateStatus(Long id, UpdateReturnStatusDTO dto) {
        ReturnRequest request = returnRequestRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Return request not found"));

        ReturnRequest.ReturnStatus currentStatus = request.getStatus();
        ReturnRequest.ReturnStatus newStatus = dto.getStatus();

        if (newStatus == null) {
            throw new IllegalArgumentException("Status must not be null");
        }

        if (currentStatus == newStatus) {
            throw new IllegalArgumentException("Return request is already in status: " + newStatus);
        }

        if (!isValidTransition(currentStatus, newStatus)) {
            throw new IllegalArgumentException(
                    "Cannot change return request status from " + currentStatus + " to " + newStatus
            );
        }

        validateRolePermission(currentStatus, newStatus);
        //check flow
        if (newStatus == ReturnRequest.ReturnStatus.REJECTED) {
            if (dto.getRejectionReason() == null || dto.getRejectionReason().trim().isEmpty()) {
                throw new IllegalArgumentException("Rejection reason is required");
            }
            request.setRejectionReason(dto.getRejectionReason().trim());
        } else {
            request.setRejectionReason(null);
        }

        request.setStatus(newStatus);
        //check role
        if (newStatus == ReturnRequest.ReturnStatus.APPROVED
                || newStatus == ReturnRequest.ReturnStatus.REJECTED
                || newStatus == ReturnRequest.ReturnStatus.COMPLETED) {
            request.setResolvedAt(LocalDateTime.now());
        }

        ReturnRequest saved = returnRequestRepo.save(request);
        return mapToDTO(saved);
    }

    private boolean isValidTransition(ReturnRequest.ReturnStatus currentStatus,
                                      ReturnRequest.ReturnStatus newStatus) {
        return switch (currentStatus) {
            case PENDING ->
                    newStatus == ReturnRequest.ReturnStatus.APPROVED
                            || newStatus == ReturnRequest.ReturnStatus.REJECTED;

            case APPROVED ->
                    newStatus == ReturnRequest.ReturnStatus.COMPLETED;

            case REJECTED, COMPLETED -> false;
        };
    }

    private void validateRolePermission(ReturnRequest.ReturnStatus currentStatus,
                                        ReturnRequest.ReturnStatus newStatus) {

        boolean isStaff = hasRole("ROLE_OPERATIONAL_STAFF");
        boolean isAdmin = hasRole("ROLE_ADMIN");

        if (!isStaff && !isAdmin) {
            throw new AccessDeniedException("You do not have permission to update return request status");
        }


        // admin và staff
        if (currentStatus == ReturnRequest.ReturnStatus.PENDING &&
                (newStatus == ReturnRequest.ReturnStatus.APPROVED
                        || newStatus == ReturnRequest.ReturnStatus.REJECTED)) {
            return;
        }

        // cả 2 đều complete
        if (currentStatus == ReturnRequest.ReturnStatus.APPROVED &&
                newStatus == ReturnRequest.ReturnStatus.COMPLETED) {
            return;
        }

        throw new AccessDeniedException("You do not have permission to update return request status");
    }

    private boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getAuthorities() == null) {
            throw new AccessDeniedException("User is not authenticated");
        }

        return auth.getAuthorities()
                .stream()
                .anyMatch(a -> role.equals(a.getAuthority()));
    }

    public ReturnRequestResponseDTO getByOrderItemId(Long orderItemId) {
        ReturnRequest request = returnRequestRepo.findByOrderItemOrderItemId(orderItemId)
                .orElseThrow(() -> new RuntimeException("Return request not found"));

        return mapToDTO(request);
    }

    public ReturnRequestResponseDTO mapToDTO(ReturnRequest request) {
        return ReturnRequestResponseDTO.builder()
                .requestId(request.getRequestId())
                .orderId(request.getOrderItem().getOrderItemId())
                .orderItemId(request.getOrderItem().getOrderItemId())
                .reason(request.getReason())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .status(request.getStatus().name())
                .requestedAt(request.getRequestedAt())
                .resolvedAt(request.getResolvedAt())
                .rejectionReason(request.getRejectionReason())
                .build();
    }
}
