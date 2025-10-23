package com.wms.goals.entity;

import com.wms.goals.utility.EntityNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = EntityNames.M_PRODUCT_TYPE)
@Data
public class MProductType {
    @Id
    @Column(name = EntityNames.MProductType.PRODUCT_TYPE_ID)
    private UUID productTypeId;

    @Column(name = EntityNames.MProductType.PRODUCT_TYPE_NAME)
    private String productTypeName;

    @Column(name = EntityNames.MProductType.ID_RISK_PROFILE)
    private UUID idRiskProfile;
}
