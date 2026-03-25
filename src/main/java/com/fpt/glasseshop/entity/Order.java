package com.fpt.glasseshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(unique = true, nullable = false, updatable = false)
    private String orderCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @CreationTimestamp
    private LocalDateTime orderDate;

    private String status;
    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "shipping_address_id")
    private Address shippingAddress;

    @ManyToOne
    @JoinColumn(name = "billing_address_id")
    private Address billingAddress;

    private String paymentStatus;
    private String paymentMethod;

    private String fullName;
    private String phone;
    private String address;
    private String note;
    
    private BigDecimal shippingFee;
    private BigDecimal voucherDiscount;
    private BigDecimal finalPrice;

    @Column(unique = true)
    private String idempotencyKey;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

}
