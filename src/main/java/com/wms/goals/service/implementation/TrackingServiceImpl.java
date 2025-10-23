package com.wms.goals.service.implementation;

import com.wms.goals.config.GoalAssumptionProperties;
import com.wms.goals.dto.response.GoalTrackingItem;
import com.wms.goals.entity.MGoal;
import com.wms.goals.repository.GoalRepository;
import com.wms.goals.repository.NavBalanceRepository;
import com.wms.goals.repository.MstRiskProfileRefRepository;
import com.wms.goals.repository.PortfolioRepository;
import com.wms.goals.service.interfacing.FinanceFormulaService;
import com.wms.goals.service.interfacing.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingServiceImpl implements TrackingService {
    private static final MathContext MC = new MathContext(16, RoundingMode.HALF_UP);

    private final GoalRepository goalRepo;
    private final NavBalanceRepository navRepo;
    private final MstRiskProfileRefRepository riskRepo;
    private final PortfolioRepository portfolioRepo;
    private final FinanceFormulaService finance;
    private final GoalAssumptionProperties assumptions;
    
    @Override
    public List<GoalTrackingItem> trackingForCustomer(UUID customerId) {
        List<MGoal> goals = goalRepo.findAllByCustomerId(customerId);
        List<GoalTrackingItem> out = new ArrayList<>();
        for (MGoal g : goals) {
            out.add(trackOne(g));
        }
        return out;
    }

    private GoalTrackingItem trackOne(MGoal g) {
        LocalDate created = g.getCreatedAt() != null ? g.getCreatedAt().toLocalDate() : LocalDate.now();
        LocalDate today = LocalDate.now();
        LocalDate deadline = g.getTargetDate();
        int totalMonths = (int) ChronoUnit.MONTHS.between(created.withDayOfMonth(1), deadline.withDayOfMonth(1));
        int elapsedMonths = (int) ChronoUnit.MONTHS.between(created.withDayOfMonth(1), today.withDayOfMonth(1));
        elapsedMonths = Math.max(0, Math.min(elapsedMonths, totalMonths));

        String profileType = riskRepo.findById(g.getRiskProfileId()).map(r -> r.getProfileType()).orElse("MODERATE");
        Double returnAnnual = assumptions.findReturnAnnual(profileType);
        if (returnAnnual == null) returnAnnual = 0.12d;
        double rMonthly = returnAnnual / 12d;
        double iMonthly = assumptions.getInflationAnnual() / 12d;

        BigDecimal targetPresent = g.getTargetAmount();
        BigDecimal fvTarget = "OTHER".equalsIgnoreCase(g.getGoalType())
                ? finance.compound(targetPresent, iMonthly, totalMonths)
                : targetPresent;
        BigDecimal pmt = finance.pmt(fvTarget, BigDecimal.ZERO, rMonthly, totalMonths);
        BigDecimal expectedToDate = expectedValueForMonths(rMonthly, elapsedMonths, pmt);

        BigDecimal actualToDate = actualValueFromPortfolio(g);

        double shortfall = expectedToDate.signum() == 0 ? 0d
                : expectedToDate.subtract(actualToDate, MC).divide(expectedToDate, MC).doubleValue();
        if (shortfall < 0) shortfall = 0;
        String status = categorize(shortfall, assumptions.getTrackingThresholdPct());
        int investedMonths = estimateInvestedMonths(actualToDate, pmt, rMonthly, elapsedMonths);
        int monthsWithoutInvestment = Math.max(0, elapsedMonths - investedMonths);
        String statusMessage = buildStatusMessage(status, monthsWithoutInvestment);

        return GoalTrackingItem.builder()
                .goalId(g.getGoalId())
                .goalType(g.getGoalType())
                .goalName(g.getGoalName())
                .createdDate(created)
                .targetDate(deadline)
                .targetAmount(g.getTargetAmount())
                .expectedValueToDate(expectedToDate)
                .actualValueToDate(actualToDate)
                .shortfallPct(shortfall)
                .status(status)
                .statusMessage(statusMessage)
                .build();
    }

    private BigDecimal expectedValueForMonths(double rMonthly, int months, BigDecimal pmt) {
        if (months <= 0) return BigDecimal.ZERO;
        if (rMonthly == 0d) {
            return pmt.multiply(BigDecimal.valueOf(months), MC);
        }
        BigDecimal pow = finance.pow(BigDecimal.valueOf(1 + rMonthly), months);
        BigDecimal numer = pow.subtract(BigDecimal.ONE, MC);
        return numer.signum() == 0 ? BigDecimal.ZERO : pmt.multiply(numer, MC).divide(BigDecimal.valueOf(rMonthly), MC);
    }

    private BigDecimal actualValueFromPortfolio(MGoal goal) {
        return portfolioRepo.findFirstByIdGoal(goal.getGoalId())
                .map(portfolio -> {
                    if (portfolio.getCurrentUnit() == null || portfolio.getCurrentUnit().signum() <= 0) {
                        return BigDecimal.ZERO;
                    }
                    UUID productId = portfolio.getIdProduct();
                    return navRepo.findTopByIdProductOrderByNavDateDesc(productId)
                            .map(nav -> portfolio.getCurrentUnit().multiply(nav.getNavPrice(), MC))
                            .orElseGet(() -> {
                                log.warn("No NAV data found for productId={}", productId);
                                return BigDecimal.ZERO;
                            });
                })
                .orElse(BigDecimal.ZERO);
    }

    private String categorize(double shortfallPct, double threshold) {
        if (shortfallPct <= threshold) return "ON_TRACK";
        if (shortfallPct <= 2 * threshold) return "AT_RISK";
        return "OFF_TRACK";
    }

    private int estimateInvestedMonths(BigDecimal actualValue, BigDecimal pmt, double rMonthly, int elapsedMonths) {
        if (actualValue == null || actualValue.signum() <= 0 || pmt == null || pmt.signum() <= 0) {
            return 0;
        }
        int invested = 0;
        BigDecimal tolerance = new BigDecimal("0.01");
        for (int month = 1; month <= elapsedMonths; month++) {
            BigDecimal expected = expectedValueForMonths(rMonthly, month, pmt);
            if (expected.subtract(actualValue, MC).compareTo(tolerance.negate()) <= 0) {
                invested = month;
            } else {
                break;
            }
        }
        return invested;
    }

    private String buildStatusMessage(String status, int monthsWithoutInvestment) {
        return switch (status) {
            case "ON_TRACK" -> "Great, keep up the pace of your investment.";
            case "OFF_TRACK" -> String.format(
                    "You haven't invested for %d months. Let's invest again to achieve your dreams..",
                    Math.max(monthsWithoutInvestment, 0)
            );
        default -> null;
        };
    }
}




