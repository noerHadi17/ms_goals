package com.wms.goals.service.common;

import com.wms.goals.config.GoalAssumptionProperties;
import com.wms.goals.dto.response.ProjectionPoint;
import com.wms.goals.entity.MstCustomerRef;
import com.wms.goals.entity.MstRiskProfileRef;
import com.wms.goals.repository.MstCustomerRefRepository;
import com.wms.goals.repository.MstRiskProfileRefRepository;
import com.wms.goals.service.interfacing.FinanceFormulaService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GoalCommon {
    private final MstCustomerRefRepository customerRepo;
    private final MstRiskProfileRefRepository riskRepo;
    private final GoalAssumptionProperties assumptions;
    private final FinanceFormulaService finance;

    public int requireTargetAge(Integer targetAge) {
        if (targetAge == null || targetAge <= 0) {
            throw new IllegalArgumentException(com.wms.goals.i18n.I18nMessageCollection.TARGET_AGE_INVALID.name());
        }
        return targetAge;
    }

    public MstCustomerRef requireCustomerWithKyc(UUID customerId) {
        MstCustomerRef cust = customerRepo.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException(com.wms.goals.i18n.I18nMessageCollection.CUSTOMER_NOT_FOUND.name()));
        boolean kycComplete = cust.getNik() != null && !"-".equals(cust.getNik())
                && cust.getPob() != null && !"-".equals(cust.getPob())
                && cust.getDob() != null;
        if (!kycComplete) {
            throw new IllegalArgumentException(com.wms.goals.i18n.I18nMessageCollection.KYC_REQUIRED.name());
        }
        return cust;
    }

    public RiskProfile requireRiskProfile(MstCustomerRef cust) {
        UUID riskProfileId = cust.getIdRiskProfile();
        if (riskProfileId == null) {
            throw new IllegalArgumentException(com.wms.goals.i18n.I18nMessageCollection.CRP_REQUIRED.name());
        }
        MstRiskProfileRef rp = riskRepo.findById(riskProfileId)
                .orElseThrow(() -> new IllegalArgumentException(com.wms.goals.i18n.I18nMessageCollection.RISK_PROFILE_NOT_FOUND.name()));
        String profileType = rp.getProfileType();
        Double returnAnnual = assumptions.findReturnAnnual(profileType);
        if (returnAnnual == null) {
            throw new IllegalArgumentException(com.wms.goals.i18n.I18nMessageCollection.PROFILE_ASSUMPTION_NOT_CONFIGURED.name());
        }
        return new RiskProfile(riskProfileId, profileType, returnAnnual);
    }

    public TargetPlan computeTargetPlan(LocalDate dob, int targetAge) {
        LocalDate now = LocalDate.now();
        int ageNow = Period.between(dob, now).getYears();
        if (targetAge <= ageNow || targetAge > 80) {
            throw new IllegalArgumentException(com.wms.goals.i18n.I18nMessageCollection.TARGET_AGE_INVALID.name());
        }
        LocalDate targetDate = LocalDate.of(dob.getYear() + targetAge, 12, 31);
        int months = (int) ChronoUnit.MONTHS.between(now.withDayOfMonth(1), targetDate.withDayOfMonth(1));
        double monthlyInflation = assumptions.getInflationAnnual() / 12d;
        return new TargetPlan(targetDate, months, monthlyInflation);
    }

    public List<ProjectionPoint> buildProjections(LocalDate start, int months,
                                                  BigDecimal pmt, double rMonthly,
                                                  BigDecimal targetFV) {
        int step;
        if (months <= 24) step = 3;
        else if (months <= 60) step = 6;
        else step = 12;

        List<ProjectionPoint> list = new ArrayList<>();
        java.math.MathContext MC = new java.math.MathContext(16, java.math.RoundingMode.HALF_UP);
        BigDecimal onePlusR = BigDecimal.valueOf(1 + rMonthly);

        for (int n = 0; n <= months; n += step) {
            BigDecimal value;
            if (rMonthly == 0d) {
                value = pmt.multiply(BigDecimal.valueOf(n), MC);
            } else {
                BigDecimal pow = finance.pow(onePlusR, n);
                BigDecimal numer = pow.subtract(BigDecimal.ONE, MC);
                value = numer.signum() == 0 ? BigDecimal.ZERO : pmt.multiply(numer, MC).divide(BigDecimal.valueOf(rMonthly), MC);
            }
            LocalDate date = start.withDayOfMonth(1).plusMonths(n);
            double progress = targetFV.signum() == 0 ? 0d : value.divide(targetFV, MC).doubleValue();
            list.add(ProjectionPoint.builder()
                    .month(n)
                    .date(date.toString())
                    .value(value)
                    .progress(Math.max(0d, Math.min(1d, progress)))
                    .build());
        }
        if ((months % step) != 0) { // ensure last point at target
            int n = months;
            BigDecimal value;
            if (rMonthly == 0d) {
                value = pmt.multiply(BigDecimal.valueOf(n), MC);
            } else {
                BigDecimal pow = finance.pow(onePlusR, n);
                BigDecimal numer = pow.subtract(BigDecimal.ONE, MC);
                value = numer.signum() == 0 ? BigDecimal.ZERO : pmt.multiply(numer, MC).divide(BigDecimal.valueOf(rMonthly), MC);
            }
            LocalDate date = start.withDayOfMonth(1).plusMonths(n);
            double progress = targetFV.signum() == 0 ? 0d : value.divide(targetFV, MC).doubleValue();
            list.add(ProjectionPoint.builder()
                    .month(n)
                    .date(date.toString())
                    .value(value)
                    .progress(Math.max(0d, Math.min(1d, progress)))
                    .build());
        }
        return list;
    }

    @Getter
    public static class RiskProfile {
        private final UUID riskProfileId;
        private final String profileType;
        private final double returnAnnual;

        public RiskProfile(UUID riskProfileId, String profileType, double returnAnnual) {
            this.riskProfileId = riskProfileId;
            this.profileType = profileType;
            this.returnAnnual = returnAnnual;
        }
    }

    @Getter
    public static class TargetPlan {
        private final LocalDate targetDate;
        private final int months;
        private final double monthlyInflation;

        public TargetPlan(LocalDate targetDate, int months, double monthlyInflation) {
            this.targetDate = targetDate;
            this.months = months;
            this.monthlyInflation = monthlyInflation;
        }

        public double monthlyReturn(double annual) { return annual / 12d; }
    }
}



