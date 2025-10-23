package com.wms.goals.repository;

import com.wms.goals.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {
    Optional<Portfolio> findFirstByIdGoal(UUID idGoal);
}
