package com.wms.goals.entity;

import com.wms.goals.utility.EntityNames;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = EntityNames.M_GOALS)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MGoal {
    @Id
    @Column(name = EntityNames.MGoals.GOAL_ID)
    private UUID goalId;

    @Column(name = EntityNames.MGoals.ID_CUSTOMER)
    private UUID customerId;

    @Column(name = EntityNames.MGoals.GOAL_TYPE)
    private String goalType;

    @Column(name = EntityNames.MGoals.TARGET_AMOUNT)
    private BigDecimal targetAmount;

    @Column(name = EntityNames.MGoals.TARGET_DATE)
    private LocalDate targetDate;

    @Column(name = EntityNames.MGoals.ID_RISK_PROFILE)
    private UUID riskProfileId;

    @Column(name = EntityNames.MGoals.GOAL_NAME)
    private String goalName;

    @Column(name = EntityNames.MGoals.CREATED_AT)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist(){
        if (goalId == null) goalId = UUID.randomUUID();
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }
}
