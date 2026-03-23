package com.fpt.glasseshop.service;

import com.fpt.glasseshop.entity.*;
import com.fpt.glasseshop.entity.dto.*;
import com.fpt.glasseshop.exception.ResourceNotFoundException;
import com.fpt.glasseshop.repository.AddressRepository;
import com.fpt.glasseshop.repository.CartRepository;
import com.fpt.glasseshop.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final CartService cartService;

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

    @Transactional
    public OrderDTO createOrderFromCart(UserAccount user, CreateOrderRequest request) {
        // 1. Get User's Cart
        Cart cart = cartRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + user.getUserId()));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot create order from an empty cart");
        }

        // 2. Resolve Addresses
        Address shippingAddress = addressRepository.findById(request.getShippingAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Shipping address not found"));
        Address billingAddress = addressRepository.findById(request.getBillingAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Billing address not found"));

        // 3. Create Order Object
        BigDecimal totalPrice = BigDecimal.ZERO;
        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .billingAddress(billingAddress)
                .status("PENDING")
                .paymentStatus("UNPAID")
                .paymentMethod(request.getPaymentMethod())
                .orderDate(LocalDateTime.now())
                .orderItems(new ArrayList<>())
                .build();

        // 4. Create OrderItems from CartItems and Calculate Total
        for (CartItem cartItem : cart.getItems()) {
            BigDecimal variantPrice = (cartItem.getVariant().getProduct() != null && cartItem.getVariant().getProduct().getPrice() != null) ? cartItem.getVariant().getProduct().getPrice()
                    : BigDecimal.ZERO;
            BigDecimal lensPrice = (cartItem.getLensOption() != null && cartItem.getLensOption().getPrice() != null)
                    ? cartItem.getLensOption().getPrice()
                    : BigDecimal.ZERO;
            BigDecimal unitPrice = variantPrice.add(lensPrice);
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            totalPrice = totalPrice.add(subtotal);

            // Find matching request item to get prescription if provided
            PrescriptionDTO prescriptionDTO = null;
            if (request.getItems() != null) {
                prescriptionDTO = request.getItems().stream()
                        .filter(ri -> ri.getVariantId().equals(cartItem.getVariant().getVariantId()) &&
                                ((ri.getLensOptionId() == null && cartItem.getLensOption() == null) ||
                                        (ri.getLensOptionId() != null && cartItem.getLensOption() != null
                                                && ri.getLensOptionId()
                                                        .equals(cartItem.getLensOption().getLensOptionId()))))
                        .map(OrderItemRequest::getPrescription)
                        .findFirst()
                        .orElse(null);
            }

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .variant(cartItem.getVariant())
                    .variantId(cartItem.getVariant() != null ? cartItem.getVariant().getVariantId() : null)
                    .productId(cartItem.getVariant() != null && cartItem.getVariant().getProduct() != null
                            ? cartItem.getVariant().getProduct().getProductId()
                            : null)
                    .productName(cartItem.getVariant() != null && cartItem.getVariant().getProduct() != null
                            ? cartItem.getVariant().getProduct().getName()
                            : null)
                    .variantColor(cartItem.getVariant() != null ? cartItem.getVariant().getColor() : null)
                    .variantSize(cartItem.getVariant() != null ? cartItem.getVariant().getFrameSize() : null)
                    .imageUrl(cartItem.getVariant() != null ? cartItem.getVariant().getImageUrl() : null)
                    .lensOption(cartItem.getLensOption())
                    .lensOptionId(cartItem.getLensOption() != null ? cartItem.getLensOption().getLensOptionId() : null)
                    .lensType(cartItem.getLensOption() != null ? cartItem.getLensOption().getType() : null)
                    .lensPrice(lensPrice)
                    .lensCoating(cartItem.getLensOption() != null ? cartItem.getLensOption().getCoating() : null)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(unitPrice)
                    .fulfillmentType(prescriptionDTO != null ? "PRESCRIPTION" : "IN_STOCK")
                    .build();

            // Handle prescription if it exists
            if (prescriptionDTO != null) {
                Prescription p = Prescription.builder()
                        .orderItem(orderItem)
                        .sphLeft(prescriptionDTO.getSphLeft())
                        .sphRight(prescriptionDTO.getSphRight())
                        .cylLeft(prescriptionDTO.getCylLeft())
                        .cylRight(prescriptionDTO.getCylRight())
                        .axisLeft(prescriptionDTO.getAxisLeft())
                        .axisRight(prescriptionDTO.getAxisRight())
                        .pd(prescriptionDTO.getPd())
                        .doctorName(prescriptionDTO.getDoctorName())
                        .expirationDate(prescriptionDTO.getExpirationDate())
                        .status("PENDING_VERIFICATION")
                        .build();
                orderItem.setPrescription(p);
            }

            order.getOrderItems().add(orderItem);
        }

        order.setTotalPrice(totalPrice);

        // 5. Save Order (Cascade should save items)
        Order savedOrder = orderRepository.save(order);

        // 6. Clear Cart
        cartService.clearCart(user);

        return convertToDTO(savedOrder);
    }

    // Removed redundant createOrder method.
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
                .paymentMethod(order.getPaymentMethod())
                .shippingAddress(mapToAddressDTO(order.getShippingAddress()))
                .billingAddress(mapToAddressDTO(order.getBillingAddress()))
                .orderItems(order.getOrderItems() != null ? order.getOrderItems().stream()
                        .map(this::mapToItemDTO)
                        .collect(Collectors.toList()) : null)
                .totalItems(order.getOrderItems() != null ? order.getOrderItems().size() : 0)
                .build();
    }

    private AddressDTO mapToAddressDTO(com.fpt.glasseshop.entity.Address address) {
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
                .variantId(item.getVariantId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .variantColor(item.getVariantColor())
                .variantSize(item.getVariantSize())
                .imageUrl(item.getImageUrl())
                .lensType(item.getLensType())
                .lensPrice(item.getLensPrice())
                .lensCoating(item.getLensCoating())
                .lensOptionId(item.getLensOptionId())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getUnitPrice().multiply(new java.math.BigDecimal(item.getQuantity())))
                .fulfillmentType(item.getFulfillmentType())
                .itemType(item.getItemType())
                .prescription(mapToPrescriptionDTO(item.getPrescription()))
                .build();
    }

    private PrescriptionDTO mapToPrescriptionDTO(Prescription p) {
        if (p == null)
            return null;
        return PrescriptionDTO.builder()
                .prescriptionId(p.getPrescriptionId())
                .orderItemId(p.getOrderItem().getOrderItemId())
                .sphLeft(p.getSphLeft())
                .sphRight(p.getSphRight())
                .cylLeft(p.getCylLeft())
                .cylRight(p.getCylRight())
                .axisLeft(p.getAxisLeft())
                .axisRight(p.getAxisRight())
                .pd(p.getPd())
                .doctorName(p.getDoctorName())
                .expirationDate(p.getExpirationDate())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .build();
    }

}
