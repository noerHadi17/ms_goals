package com.wms.goals.entity;

import com.wms.goals.utility.EntityNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = EntityNames.PORTFOLIOS)
@Data
public class Portfolio {
    @Id
    @Column(name = EntityNames.Portfolios.PORTFOLIO_ID)
    private UUID portfolioId;

    @Column(name = EntityNames.Portfolios.ID_CUSTOMER)
    private UUID idCustomer;

    @Column(name = EntityNames.Portfolios.ID_GOAL)
    private UUID idGoal;

    @Column(name = EntityNames.Portfolios.ID_PRODUCT)
    private UUID idProduct;

    @Column(name = EntityNames.Portfolios.CURRENT_UNIT)
    private BigDecimal currentUnit;
}
