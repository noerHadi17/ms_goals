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
public class RetirementCreateResponse {
    private UUID goalId;
    private String goalType;
    private String goalName;
    private Integer targetAge;
    private BigDecimal targetAmountNeeded; // totalFutureNeed at retirement
    private Assumptions assumptions;
    private List<ProjectionPoint> projections;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Assumptions {
        private double returnAnnual;
        private double depositAnnual;
        private double inflationAnnual;
        private String compounding;
        private String profile;
        private Integer monthsInvest;
        private Integer monthsRetire;
        private BigDecimal futureMonthlyExpense;
        private BigDecimal totalFutureRecurringNeed;
        private BigDecimal futureLumpSum;
        private BigDecimal requiredMonthlyContribution;
    }
}
