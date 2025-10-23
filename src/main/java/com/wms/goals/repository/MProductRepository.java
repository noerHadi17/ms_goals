package com.wms.goals.repository;

import com.wms.goals.entity.MProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MProductRepository extends JpaRepository<MProduct, UUID> {
    List<MProduct> findAllByProductTypeId(UUID productTypeId);
    Optional<MProduct> findFirstByProductTypeIdOrderByProductNameAsc(UUID productTypeId);
}

