package com.fpt.glasseshop.repository;

import com.fpt.glasseshop.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    Optional<Prescription> findByOrderItem_OrderItemId(Long orderItemId);
}