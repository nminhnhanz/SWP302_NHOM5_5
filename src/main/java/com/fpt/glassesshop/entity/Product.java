package com.fpt.glassesshop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

    private Boolean isPrescriptionSupported;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum ProductType {
        FRAME, LENS, ACCESSORY
    }
}
