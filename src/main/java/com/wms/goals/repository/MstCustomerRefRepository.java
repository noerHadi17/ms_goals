package com.wms.goals.repository;

import com.wms.goals.entity.MstCustomerRef;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MstCustomerRefRepository extends JpaRepository<MstCustomerRef, UUID> {
}

