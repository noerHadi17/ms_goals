package com.wms.goals.repository;

import com.wms.goals.entity.MstRiskProfileRef;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MstRiskProfileRefRepository extends JpaRepository<MstRiskProfileRef, UUID> {
    Optional<MstRiskProfileRef> findByProfileType(String profileType);
}

