package com.wms.goals.repository;

import com.wms.goals.entity.MProductType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MProductTypeRepository extends JpaRepository<MProductType, UUID> {
    Optional<MProductType> findFirstByIdRiskProfile(UUID idRiskProfile);
    List<MProductType> findAllByIdRiskProfile(UUID idRiskProfile);
}

