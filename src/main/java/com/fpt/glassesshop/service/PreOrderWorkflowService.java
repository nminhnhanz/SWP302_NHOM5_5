package com.fpt.glassesshop.service;

import com.fpt.glassesshop.constant.PreOrderStatus;
import com.fpt.glassesshop.entity.OrderItem;
import com.fpt.glassesshop.entity.PreOrder;
import com.fpt.glassesshop.repository.OrderItemRepository;
import com.fpt.glassesshop.repository.PreOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PreOrderWorkflowService {

    private final PreOrderRepository preOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderWorkflowService orderWorkflowService;

    @Transactional
    public void updateStatus(Long orderItemId, String statusRaw) {
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("OrderItem not found: " + orderItemId));

        PreOrder preOrder = preOrderRepository.findByOrderItem_OrderItemId(orderItemId)
                .orElseThrow(() -> new RuntimeException("PreOrder not found for orderItem: " + orderItemId));

        PreOrderStatus newStatus = PreOrderStatus.from(statusRaw);
        preOrder.setStatus(newStatus.name());
        preOrderRepository.save(preOrder);

        orderWorkflowService.recompute(item.getOrder().getOrderId());
    }
}