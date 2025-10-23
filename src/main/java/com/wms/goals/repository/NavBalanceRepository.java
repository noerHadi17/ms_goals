package com.wms.goals.repository;

import com.wms.goals.entity.NavBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface NavBalanceRepository extends JpaRepository<NavBalance, UUID> {
    Optional<NavBalance> findTopByIdProductOrderByNavDateDesc(UUID productId);
    Optional<NavBalance> findTopByIdProductAndNavDateLessThanEqualOrderByNavDateDesc(UUID productId, LocalDate navDate);
}

