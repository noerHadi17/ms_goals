package com.wms.goals.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalSummary {
    private UUID goalId;
    private String goalType;
    private String goalName;
    private BigDecimal targetAmount;
    private LocalDate targetDate;
    private UUID riskProfileId;
    private OffsetDateTime createdAt;
}

