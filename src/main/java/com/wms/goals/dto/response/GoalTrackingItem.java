package com.wms.goals.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalTrackingItem {
    private UUID goalId;
    private String goalType;
    private String goalName;
    private LocalDate createdDate;
    private LocalDate targetDate;
    private BigDecimal targetAmount;
    private BigDecimal expectedValueToDate;
    private BigDecimal actualValueToDate;
    private double shortfallPct;
    private String status;
    private String statusMessage;
}
