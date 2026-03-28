package com.fpt.glasseshop.repository;

import com.fpt.glasseshop.entity.ReturnRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {
    boolean existsByOrderItemOrderItemId(Long orderId);
    Optional<ReturnRequest> findByOrderItemOrderItemId(Long orderItemId);
}
