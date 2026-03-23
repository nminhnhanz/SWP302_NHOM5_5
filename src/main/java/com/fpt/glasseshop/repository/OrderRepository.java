package com.fpt.glasseshop.repository;

import com.fpt.glasseshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserUserId(Long userId);

    @Query("""
            SELECT COALESCE(SUM(o.totalPrice), 0)
            FROM Order o 
            WHERE o.paymentStatus = 'PAID'
            AND o.orderDate >= :from
            AND o.orderDate <= :to           
           """ )
    BigDecimal calculateRevenueBetween(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
        SELECT COUNT(o)
        FROM Order o
        WHERE o.paymentStatus = 'PAID'
        AND o.orderDate >= :from
        AND o.orderDate <= :to
    """)
    Long countPaidOrdersBetween(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
