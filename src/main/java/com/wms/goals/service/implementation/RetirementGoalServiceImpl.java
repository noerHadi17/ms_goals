package com.wms.goals.service.implementation;

import com.wms.goals.audit.AuditProducer;
import com.wms.goals.config.GoalAssumptionProperties;
import com.wms.goals.dto.request.CreateRetirementGoalRequest;
import com.wms.goals.dto.request.EditRetirementGoalRequest;
import com.wms.goals.dto.response.ProjectionPoint;
import com.wms.goals.dto.response.RetirementCreateResponse;
import com.wms.goals.entity.MGoal;
import com.wms.goals.entity.MstCustomerRef;
import com.wms.goals.repository.GoalRepository;
import com.wms.goals.service.common.GoalCommon;
import com.wms.goals.service.interfacing.FinanceFormulaService;
import com.wms.goals.service.interfacing.RetirementGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RetirementGoalServiceImpl implements RetirementGoalService {
    private final GoalRepository repo;
    private final FinanceFormulaService finance;
    private final GoalAssumptionProperties assumptions;
    private final AuditProducer auditProducer;
    private final GoalCommon common;

    @Transactional
    public RetirementCreateResponse createRetirement(CreateRetirementGoalRequest req, UUID customerId){
        int targetAge = common.requireTargetAge(req.getTargetAge());
        MstCustomerRef cust = common.requireCustomerWithKyc(customerId);
        GoalCommon.RiskProfile rp = common.requireRiskProfile(cust);
        GoalCommon.TargetPlan tp = common.computeTargetPlan(cust.getDob(), targetAge);

        int monthsInvest = tp.getMonths();
        int monthsRetire = req.getYearsAfterRetirement() * 12;
        double rMonthly = tp.monthlyReturn(rp.getReturnAnnual());
        double dMonthly = assumptions.getDepositAnnual() / 12d;

        BigDecimal futureMonthlyExpense = finance.compound(req.getMonthlyExpense(), tp.getMonthlyInflation(), monthsInvest);
        java.math.MathContext MC = new java.math.MathContext(16, java.math.RoundingMode.HALF_UP);
        BigDecimal onePlusD = BigDecimal.valueOf(1 + dMonthly);
        BigDecimal powD = finance.pow(onePlusD, monthsRetire);
        BigDecimal annuityFactor = BigDecimal.ONE.subtract(BigDecimal.ONE.divide(powD, MC), MC)
                .divide(BigDecimal.valueOf(dMonthly), MC);
        BigDecimal totalFutureNeed = futureMonthlyExpense.multiply(annuityFactor, MC);

        BigDecimal fvLump = finance.compound(BigDecimal.ZERO, rMonthly, monthsInvest);
        BigDecimal pmt = finance.pmt(totalFutureNeed, fvLump, rMonthly, monthsInvest);
        List<ProjectionPoint> projections = common.buildProjections(LocalDate.now(), monthsInvest, pmt, rMonthly, totalFutureNeed);

        MGoal goal = repo.save(MGoal.builder()
                .customerId(customerId)
                .goalType(req.getGoalType())
                .goalName(req.getGoalName())
                .targetAmount(totalFutureNeed)
                .targetDate(tp.getTargetDate())
                .riskProfileId(rp.getRiskProfileId())
                .createdAt(java.time.OffsetDateTime.now())
                .build());

        auditProducer.publish("GOAL_CREATED","SUCCESS", customerId, cust.getEmail(), goal.getGoalId(),
                "Retirement goal created",
                Map.of(
                        "returnAnnual", rp.getReturnAnnual(),
                        "depositAnnual", assumptions.getDepositAnnual(),
                        "inflationAnnual", assumptions.getInflationAnnual(),
                        "profile", rp.getProfileType(),
                        "monthsInvest", monthsInvest,
                        "monthsRetire", monthsRetire,
                        "futureMonthlyExpense", futureMonthlyExpense,
                        "totalFutureNeed", totalFutureNeed,
                        "futureLumpSum", fvLump,
                        "requiredMonthlyContribution", pmt
                )
        );

        return RetirementCreateResponse.builder()
                .goalId(goal.getGoalId())
                .goalType(goal.getGoalType())
                .goalName(goal.getGoalName())
                .targetAge(targetAge)
                .targetAmountNeeded(totalFutureNeed)
                .assumptions(RetirementCreateResponse.Assumptions.builder()
                        .returnAnnual(rp.getReturnAnnual())
                        .inflationAnnual(assumptions.getInflationAnnual())
                        .depositAnnual(assumptions.getDepositAnnual())
                        .compounding(assumptions.getCompounding())
                        .profile(rp.getProfileType())
                        .requiredMonthlyContribution(pmt)
                        .monthsInvest(monthsInvest)
                        .monthsRetire(monthsRetire)
                        .futureMonthlyExpense(futureMonthlyExpense)
                        .totalFutureRecurringNeed(totalFutureNeed)
                        .futureLumpSum(fvLump)
                        .build())
                .projections(projections)
                .build();
    }

    @Transactional(readOnly = true)
    public RetirementCreateResponse simulateRetirement(CreateRetirementGoalRequest req, UUID customerId){
        int targetAge = common.requireTargetAge(req.getTargetAge());
        MstCustomerRef cust = common.requireCustomerWithKyc(customerId);
        GoalCommon.RiskProfile rp = common.requireRiskProfile(cust);
        GoalCommon.TargetPlan tp = common.computeTargetPlan(cust.getDob(), targetAge);

        int monthsInvest = tp.getMonths();
        int monthsRetire = req.getYearsAfterRetirement() * 12;
        double rMonthly = tp.monthlyReturn(rp.getReturnAnnual());
        double dMonthly = assumptions.getDepositAnnual() / 12d;

        BigDecimal futureMonthlyExpense = finance.compound(req.getMonthlyExpense(), tp.getMonthlyInflation(), monthsInvest);
        java.math.MathContext MC = new java.math.MathContext(16, java.math.RoundingMode.HALF_UP);
        BigDecimal onePlusD = BigDecimal.valueOf(1 + dMonthly);
        BigDecimal powD = finance.pow(onePlusD, monthsRetire);
        BigDecimal annuityFactor = BigDecimal.ONE.subtract(BigDecimal.ONE.divide(powD, MC), MC)
                .divide(BigDecimal.valueOf(dMonthly), MC);
        BigDecimal totalFutureNeed = futureMonthlyExpense.multiply(annuityFactor, MC);
        BigDecimal fvLump = finance.compound(BigDecimal.ZERO, rMonthly, monthsInvest);
        BigDecimal pmt = finance.pmt(totalFutureNeed, fvLump, rMonthly, monthsInvest);
        List<ProjectionPoint> projections = common.buildProjections(LocalDate.now(), monthsInvest, pmt, rMonthly, totalFutureNeed);

        return RetirementCreateResponse.builder()
                .goalId(null)
                .goalType(req.getGoalType())
                .goalName(req.getGoalName())
                .targetAge(targetAge)
                .targetAmountNeeded(totalFutureNeed)
                .assumptions(RetirementCreateResponse.Assumptions.builder()
                        .returnAnnual(rp.getReturnAnnual())
                        .inflationAnnual(assumptions.getInflationAnnual())
                        .depositAnnual(assumptions.getDepositAnnual())
                        .compounding(assumptions.getCompounding())
                        .profile(rp.getProfileType())
                        .requiredMonthlyContribution(pmt)
                        .monthsInvest(monthsInvest)
                        .monthsRetire(monthsRetire)
                        .futureMonthlyExpense(futureMonthlyExpense)
                        .totalFutureRecurringNeed(totalFutureNeed)
                        .futureLumpSum(fvLump)
                        .build())
                .projections(projections)
                .build();
    }

    @Transactional
    public RetirementCreateResponse editRetirement(UUID goalId, UUID customerId, EditRetirementGoalRequest req) {
        MGoal g = repo.findById(goalId).orElseThrow(() -> new IllegalArgumentException("GOAL_NOT_FOUND"));
        if (!g.getCustomerId().equals(customerId)) throw new IllegalArgumentException("AUTH_INVALID_CREDENTIALS");

        int targetAge = common.requireTargetAge(req.getTargetAge());
        MstCustomerRef customer = common.requireCustomerWithKyc(customerId);
        GoalCommon.RiskProfile rp = common.requireRiskProfile(customer);
        GoalCommon.TargetPlan tp = common.computeTargetPlan(customer.getDob(), targetAge);

        int monthsInvest = tp.getMonths();
        int monthsRetire = req.getYearsAfterRetirement() * 12;
        double rMonthly = tp.monthlyReturn(rp.getReturnAnnual());
        double dMonthly = assumptions.getDepositAnnual() / 12d;

        BigDecimal futureMonthlyExpense = finance.compound(req.getMonthlyExpense(), tp.getMonthlyInflation(), monthsInvest);
        java.math.MathContext MC = new java.math.MathContext(16, java.math.RoundingMode.HALF_UP);
        BigDecimal onePlusD = BigDecimal.valueOf(1 + dMonthly);
        BigDecimal powD = finance.pow(onePlusD, monthsRetire);
        BigDecimal annuityFactor = BigDecimal.ONE.subtract(BigDecimal.ONE.divide(powD, MC), MC)
                .divide(BigDecimal.valueOf(dMonthly), MC);
        BigDecimal totalFutureNeed = futureMonthlyExpense.multiply(annuityFactor, MC);
        BigDecimal fvLump = finance.compound(BigDecimal.ZERO, rMonthly, monthsInvest);
        BigDecimal pmt = finance.pmt(totalFutureNeed, fvLump, rMonthly, monthsInvest);
        List<ProjectionPoint> projections = common.buildProjections(LocalDate.now(), monthsInvest, pmt, rMonthly, totalFutureNeed);

        g.setTargetAmount(totalFutureNeed);
        g.setTargetDate(tp.getTargetDate());
        g.setRiskProfileId(rp.getRiskProfileId());
        g = repo.save(g);

        auditProducer.publish("GOAL_UPDATED","SUCCESS", customerId, customer.getEmail(), g.getGoalId(),
                "Goal RETIREMENT updated",
                Map.of(
                        "returnAnnual", rp.getReturnAnnual(),
                        "depositAnnual", assumptions.getDepositAnnual(),
                        "inflationAnnual", assumptions.getInflationAnnual(),
                        "profile", rp.getProfileType(),
                        "monthsInvest", monthsInvest,
                        "monthsRetire", monthsRetire,
                        "futureMonthlyExpense", futureMonthlyExpense,
                        "totalFutureNeed", totalFutureNeed,
                        "futureLumpSum", fvLump,
                        "requiredMonthlyContribution", pmt
                ));

        return RetirementCreateResponse.builder()
                .goalId(g.getGoalId())
                .goalType(g.getGoalType())
                .goalName(g.getGoalName())
                .targetAge(targetAge)
                .targetAmountNeeded(totalFutureNeed)
                .assumptions(RetirementCreateResponse.Assumptions.builder()
                        .returnAnnual(rp.getReturnAnnual())
                        .depositAnnual(assumptions.getDepositAnnual())
                        .inflationAnnual(assumptions.getInflationAnnual())
                        .compounding(assumptions.getCompounding())
                        .profile(rp.getProfileType())
                        .monthsInvest(monthsInvest)
                        .monthsRetire(monthsRetire)
                        .futureMonthlyExpense(futureMonthlyExpense)
                        .totalFutureRecurringNeed(totalFutureNeed)
                        .futureLumpSum(fvLump)
                        .requiredMonthlyContribution(pmt)
                        .build())
                .projections(projections)
                .build();
    }
}





