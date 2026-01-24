package com.fpt.glassesshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Enumerated(EnumType.STRING)
    private ProductType productType;

    private String name;
    private String brand;

    @Column(columnDefinition = "TEXT")
    private String description;

    private boolean isPrescriptionSupported;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum ProductType {
        FRAME, LENS, ACCESSORY
    }
}
