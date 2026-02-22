package com.fpt.glassesshop.service;

import com.fpt.glassesshop.constant.PrescriptionStatus;
import com.fpt.glassesshop.entity.Notification;
import com.fpt.glassesshop.entity.OrderItem;
import com.fpt.glassesshop.entity.Prescription;
import com.fpt.glassesshop.repository.NotificationRepository;
import com.fpt.glassesshop.repository.OrderItemRepository;
import com.fpt.glassesshop.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PrescriptionApprovalService {

    private final PrescriptionRepository prescriptionRepository;
    private final OrderItemRepository orderItemRepository;
    private final NotificationRepository notificationRepository;
    private final OrderWorkflowService orderWorkflowService;

    @Transactional
    public void approve(Long orderItemId) {
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("OrderItem not found: " + orderItemId));

        Prescription prescription = prescriptionRepository.findByOrderItem_OrderItemId(orderItemId)
                .orElseThrow(() -> new RuntimeException("Prescription not found for orderItem: " + orderItemId));

        PrescriptionStatus current = PrescriptionStatus.from(prescription.getStatus());
        if (current != PrescriptionStatus.PENDING) {
            throw new RuntimeException("Prescription must be PENDING to approve. Current=" + prescription.getStatus());
        }

        prescription.setStatus(PrescriptionStatus.APPROVED.name());
        prescriptionRepository.save(prescription);

        Long orderId = item.getOrder().getOrderId();
        orderWorkflowService.recompute(orderId);

        notificationRepository.save(Notification.builder()
                .user(item.getOrder().getUser())
                .title("Đơn kính đã được duyệt")
                .message("Order #" + orderId + " - orderItem #" + orderItemId + " đã được duyệt đơn kính.")
                .isRead(false)
                .build());
    }

    @Transactional
    public void reject(Long orderItemId, String reason) {
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("OrderItem not found: " + orderItemId));

        Prescription prescription = prescriptionRepository.findByOrderItem_OrderItemId(orderItemId)
                .orElseThrow(() -> new RuntimeException("Prescription not found for orderItem: " + orderItemId));

        PrescriptionStatus current = PrescriptionStatus.from(prescription.getStatus());
        if (current != PrescriptionStatus.PENDING) {
            throw new RuntimeException("Prescription must be PENDING to reject. Current=" + prescription.getStatus());
        }

        prescription.setStatus(PrescriptionStatus.REJECTED.name());
        prescriptionRepository.save(prescription);

        Long orderId = item.getOrder().getOrderId();
        orderWorkflowService.recompute(orderId);

        notificationRepository.save(Notification.builder()
                .user(item.getOrder().getUser())
                .title("Đơn kính bị từ chối")
                .message("Order #" + orderId + " - orderItem #" + orderItemId + " bị từ chối: " + reason)
                .isRead(false)
                .build());
    }
}