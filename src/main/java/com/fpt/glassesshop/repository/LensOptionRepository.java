package com.fpt.glassesshop.repository;

import com.fpt.glassesshop.entity.LensOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LensOptionRepository extends JpaRepository<LensOption, Long> {
}
