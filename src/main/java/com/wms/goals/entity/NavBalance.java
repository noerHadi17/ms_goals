package com.wms.goals.entity;

import com.wms.goals.utility.EntityNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = EntityNames.NAV_BALANCE)
@Data
public class NavBalance {
    @Id
    @Column(name = EntityNames.NavBalance.NAV_ID)
    private UUID navId;

    @Column(name = EntityNames.NavBalance.ID_PRODUCT)
    private UUID idProduct;

    @Column(name = EntityNames.NavBalance.NAV_PRICE)
    private BigDecimal navPrice;

    @Column(name = EntityNames.NavBalance.NAV_DATE)
    private LocalDate navDate;

    @Column(name = EntityNames.NavBalance.CREATED_AT)
    private OffsetDateTime createdAt;
}
