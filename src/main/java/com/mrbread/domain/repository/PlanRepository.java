package com.mrbread.domain.repository;

import com.mrbread.domain.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlanRepository extends JpaRepository<Plan, UUID> {
    
    Optional<Plan> findByName(String name);
    
    List<Plan> findByIsActiveTrue();
    
    @Query("SELECT p FROM Plan p WHERE p.isActive = true AND p.billingCycle = :billingCycle")
    List<Plan> findActiveByBillingCycle(@Param("billingCycle") String billingCycle);
    
    @Query("SELECT p FROM Plan p WHERE p.isActive = true ORDER BY p.price ASC")
    List<Plan> findActiveOrderByPrice();

    Optional<Plan> findByPrice(BigDecimal price);

}
