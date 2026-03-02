package com.fpt.glasseshop.repository;

import com.fpt.glasseshop.entity.PreOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PreOrderRepository extends JpaRepository<PreOrder, Long> {
    Optional<PreOrder> findByOrderItem_OrderItemId(Long orderItemId);
}