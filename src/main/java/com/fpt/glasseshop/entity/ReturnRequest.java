package com.fpt.glasseshop.entity;

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
@Table(name = "return_request")
public class ReturnRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderItem orderItem;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    private ReturnStatus status;

    @CreationTimestamp
    private LocalDateTime requestedAt;
    private LocalDateTime resolvedAt;

    public enum ReturnStatus {
        PENDING, APPROVED, REJECTED, COMPLETED
    }
    // Mô tả chi tiết
    @Column(columnDefinition = "TEXT")
    private String description;

    // Ảnh minh chứng
    @Column(columnDefinition = "TEXT")
    private String imageUrl;

}
