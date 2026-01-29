package com.fpt.glassesshop.repository;

import com.fpt.glassesshop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderOrderId(Long orderId);

    @Query("SELECT oi FROM OrderItem oi WHERE EXISTS (SELECT p FROM Prescription p WHERE p.orderItem = oi)")
    List<OrderItem> findAllPrescriptionItems();

    @Query("SELECT oi FROM OrderItem oi WHERE EXISTS (SELECT po FROM PreOrder po WHERE po.orderItem = oi)")
    List<OrderItem> findAllPreOrderItems();

    @Query("SELECT oi FROM OrderItem oi WHERE NOT EXISTS (SELECT p FROM Prescription p WHERE p.orderItem = oi) AND NOT EXISTS (SELECT po FROM PreOrder po WHERE po.orderItem = oi)")
    List<OrderItem> findAllInStockItems();

    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE EXISTS (SELECT p FROM Prescription p WHERE p.orderItem = oi)")
    long countPrescriptionItems();

    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE EXISTS (SELECT po FROM PreOrder po WHERE po.orderItem = oi)")
    long countPreOrderItems();

    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE NOT EXISTS (SELECT p FROM Prescription p WHERE p.orderItem = oi) AND NOT EXISTS (SELECT po FROM PreOrder po WHERE po.orderItem = oi)")
    long countInStockItems();

}
