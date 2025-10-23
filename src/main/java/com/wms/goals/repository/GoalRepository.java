package com.wms.goals.repository;

import com.wms.goals.entity.MGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GoalRepository extends JpaRepository<MGoal, UUID> {
    List<MGoal> findAllByCustomerId(UUID customerId);
    List<MGoal> findAllByCustomerIdOrderByCreatedAtDesc(UUID customerId);
}
