package com.fpt.glassesshop.service;

import com.fpt.glassesshop.entity.Order;
import com.fpt.glassesshop.entity.OrderItem;
import com.fpt.glassesshop.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.fpt.glassesshop.entity.dto.AddressDTO;
import com.fpt.glassesshop.entity.dto.OrderDTO;
import com.fpt.glassesshop.entity.dto.OrderItemDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<OrderDTO> getOrdersDTOByUserId(Long userId) {
        return getOrdersByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getAllOrdersDTO() {
        return orderRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public Optional<OrderDTO> getOrderDTOById(Long orderId) {
        return getOrderById(orderId).map(this::convertToDTO);
    }

    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    public OrderDTO createOrder(OrderDTO dto) {
        // Basic implementation for now - just saving the order entity
        // Real implementation would handle items, addresses, etc.
        Order order = Order.builder()
                .status(dto.getStatus() != null ? dto.getStatus() : "PENDING")
                .paymentStatus(dto.getPaymentStatus() != null ? dto.getPaymentStatus() : "UNPAID")
                .totalPrice(dto.getTotalPrice())
                .orderDate(LocalDateTime.now())
                .build();

        // Handle User
        if (dto.getUserId() != null) {
            order.setUser(com.fpt.glassesshop.entity.UserAccount.builder().userId(dto.getUserId()).build());
        }

        Order saved = orderRepository.save(order);
        return convertToDTO(saved);
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemService.getOrderItemsByOrderId(orderId);
    }

    private OrderDTO convertToDTO(Order order) {
        return OrderDTO.builder()
                .orderId(order.getOrderId())
                .userId(order.getUser() != null ? order.getUser().getUserId() : null)
                .userName(order.getUser() != null ? order.getUser().getName() : null)
                .userEmail(order.getUser() != null ? order.getUser().getEmail() : null)
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .paymentStatus(order.getPaymentStatus())
                .shippingAddress(mapToAddressDTO(order.getShippingAddress()))
                .billingAddress(mapToAddressDTO(order.getBillingAddress()))
                .orderItems(order.getOrderItems() != null ? order.getOrderItems().stream()
                        .map(this::mapToItemDTO)
                        .collect(Collectors.toList()) : null)
                .totalItems(order.getOrderItems() != null ? order.getOrderItems().size() : 0)
                .build();
    }

    private AddressDTO mapToAddressDTO(com.fpt.glassesshop.entity.Address address) {
        if (address == null)
            return null;
        return AddressDTO.builder()
                .addressId(address.getAddressId())
                .street(address.getStreet())
                .city(address.getCity())
                .country(address.getCountry())
                .build();
    }

    private OrderItemDTO mapToItemDTO(OrderItem item) {
        if (item == null)
            return null;
        return OrderItemDTO.builder()
                .orderItemId(item.getOrderItemId())
                .variantId(item.getVariant() != null ? item.getVariant().getVariantId() : null)
                .productName(item.getVariant() != null && item.getVariant().getProduct() != null
                        ? item.getVariant().getProduct().getName()
                        : null)
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getUnitPrice().multiply(new java.math.BigDecimal(item.getQuantity())))
                .fulfillmentType(item.getFulfillmentType())
                .build();
    }

}
