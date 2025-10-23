package com.wms.goals.entity;

import com.wms.goals.utility.EntityNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = EntityNames.M_PRODUCTS)
@Data
public class MProduct {
    @Id
    @Column(name = EntityNames.MProducts.PRODUCT_ID)
    private UUID productId;

    @Column(name = EntityNames.MProducts.PRODUCT_NAME)
    private String productName;

    @Column(name = EntityNames.MProducts.PRODUCT_TYPE)
    private String productType;

    @Column(name = EntityNames.MProducts.NAV_PRICE)
    private BigDecimal navPrice;

    @Column(name = EntityNames.MProducts.CUT_OFF_TIME)
    private LocalTime cutOffTime;

    @Column(name = EntityNames.MProducts.PRODUCT_TYPE_ID)
    private UUID productTypeId;

    @Column(name = EntityNames.MProducts.UPDATED_AT)
    private OffsetDateTime updatedAt;
}
