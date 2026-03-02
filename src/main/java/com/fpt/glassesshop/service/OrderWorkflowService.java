package com.fpt.glassesshop.service;

import com.fpt.glassesshop.constant.OrderStatus;
import com.fpt.glassesshop.constant.PaymentStatus;
import com.fpt.glassesshop.constant.PreOrderStatus;
import com.fpt.glassesshop.constant.PrescriptionStatus;
import com.fpt.glassesshop.entity.Order;
import com.fpt.glassesshop.entity.OrderItem;
import com.fpt.glassesshop.entity.PreOrder;
import com.fpt.glassesshop.entity.Prescription;
import com.fpt.glassesshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderWorkflowService {

    private final OrderRepository orderRepository;

    /**
     * Rule:
     * 1) Any prescription REJECTED => Order REJECTED
     * 2) Any prescription PENDING => Order AWAITING_PRESCRIPTION_APPROVAL
     * 3) Any preorder WAITING_STOCK => Order PREORDER_WAITING_STOCK
     * 4) paymentStatus PAID => Order PROCESSING
     * 5) else => Order CONFIRMED
     */
    @Transactional
    public Order recompute(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // Nếu order đã kết thúc thì không tự động đổi nữa
        OrderStatus current = safeOrderStatus(order.getStatus());
        if (current == OrderStatus.CANCELLED || current == OrderStatus.DELIVERED || current == OrderStatus.REJECTED) {
            return order;
        }

        boolean pendingPrescription = false;
        boolean rejectedPrescription = false;
        boolean waitingStock = false;

        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                Prescription pr = item.getPrescription();
                if (pr != null && pr.getStatus() != null) {
                    PrescriptionStatus ps = PrescriptionStatus.from(pr.getStatus());
                    if (ps == PrescriptionStatus.PENDING) pendingPrescription = true;
                    if (ps == PrescriptionStatus.REJECTED) rejectedPrescription = true;
                }

                PreOrder po = item.getPreOrder();
                if (po != null && po.getStatus() != null) {
                    PreOrderStatus pos = PreOrderStatus.from(po.getStatus());
                    if (pos == PreOrderStatus.WAITING_STOCK) waitingStock = true;
                }
            }
        }

        // Priority: reject > pending approval > waiting stock > payment
        if (rejectedPrescription) {
            order.setStatus(OrderStatus.REJECTED.name());
            return orderRepository.save(order);
        }

        if (pendingPrescription) {
            order.setStatus(OrderStatus.AWAITING_PRESCRIPTION_APPROVAL.name());
            return orderRepository.save(order);
        }

        if (waitingStock) {
            order.setStatus(OrderStatus.PREORDER_WAITING_STOCK.name());
            return orderRepository.save(order);
        }

        // Không bị block => dựa paymentStatus
        PaymentStatus pay = safePaymentStatus(order.getPaymentStatus());
        if (pay == PaymentStatus.PAID) {
            order.setStatus(OrderStatus.PROCESSING.name());
            return orderRepository.save(order);
        }

        order.setStatus(OrderStatus.CONFIRMED.name());
        return orderRepository.save(order);
    }

    private OrderStatus safeOrderStatus(String raw) {
        try { return raw == null ? null : OrderStatus.from(raw); }
        catch (Exception e) { return null; }
    }

    private PaymentStatus safePaymentStatus(String raw) {
        try { return raw == null ? null : PaymentStatus.from(raw); }
        catch (Exception e) { return null; }
    }
}