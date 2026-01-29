package com.fpt.glassesshop.service;

import com.fpt.glassesshop.entity.Order;
import com.fpt.glassesshop.entity.OrderItem;
import com.fpt.glassesshop.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;

    public Order saveOrder(Order order) {
        Order savedOrder = orderRepository.save(order);
        if (order.getOrderItems() != null) {
            order.getOrderItems().forEach(item -> {
                item.setOrder(savedOrder);
                orderItemService.saveOrderItem(item);
            });
        }
        return savedOrder;
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserUserId(userId);
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemService.getOrderItemsByOrderId(orderId);
    }

}
