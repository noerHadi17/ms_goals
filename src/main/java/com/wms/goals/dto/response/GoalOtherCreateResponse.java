package com.wms.goals.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalOtherCreateResponse {
    private UUID goalId;
    private String goalType;
    private String goalName;
    private Integer targetYear;
    private BigDecimal targetAmountPresent;
    private Assumptions assumptions;
    private List<ProjectionPoint> projections;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Assumptions {
        private double returnAnnual;
        private double inflationAnnual;
        private String compounding;
        private String profile;
        private BigDecimal targetAmountFuture;
        private BigDecimal requiredMonthlyContribution;
        private Integer monthsInvest;
    }
}
