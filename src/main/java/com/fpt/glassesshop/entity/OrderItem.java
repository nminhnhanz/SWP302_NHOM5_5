package com.fpt.glassesshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    @ManyToOne
    @JoinColumn(name = "lens_option_id")
    private LensOption lensOption;

    private Integer quantity;
    private BigDecimal unitPrice;
    private String fulfillmentType;

    @OneToOne(mappedBy = "orderItem", cascade = CascadeType.ALL)
    private Prescription prescription;

    @OneToOne(mappedBy = "orderItem", cascade = CascadeType.ALL)
    private PreOrder preOrder;

    public String getItemType() {
        if (prescription != null)
            return "PRESCRIPTION";
        if (preOrder != null)
            return "PRE_ORDER";
        return "IN_STOCK";
    }
}
