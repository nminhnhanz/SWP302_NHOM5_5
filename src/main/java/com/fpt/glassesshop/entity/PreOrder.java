package com.fpt.glassesshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "pre_order")
public class PreOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long preorderId;

    @OneToOne
    @JoinColumn(name = "order_item_id", unique = true)
    private OrderItem orderItem;

    private LocalDate expectedArrival;
    private String supplierName;
    private String status;
}
