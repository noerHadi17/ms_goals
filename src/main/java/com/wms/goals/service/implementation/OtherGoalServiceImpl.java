package com.wms.goals.service.implementation;

import com.wms.goals.audit.AuditProducer;
import com.wms.goals.config.GoalAssumptionProperties;
import com.wms.goals.dto.request.CreateOtherGoalRequest;
import com.wms.goals.dto.request.EditOtherGoalRequest;
import com.wms.goals.dto.response.GoalOtherCreateResponse;
import com.wms.goals.dto.response.ProjectionPoint;
import com.wms.goals.entity.MGoal;
import com.wms.goals.entity.MstCustomerRef;
import com.wms.goals.repository.GoalRepository;
import com.wms.goals.service.common.GoalCommon;
import com.wms.goals.service.interfacing.FinanceFormulaService;
import com.wms.goals.service.interfacing.OtherGoalService;
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
public class OtherGoalServiceImpl implements OtherGoalService {
    private final GoalRepository repo;
    private final FinanceFormulaService finance;
    private final GoalAssumptionProperties assumptions;
    private final AuditProducer auditProducer;
    private final GoalCommon common;

    @Transactional
    public GoalOtherCreateResponse createOther(CreateOtherGoalRequest req, UUID customerId){
        int targetYear = req.getTargetYear();
        int currentYear = LocalDate.now().getYear();
        if (targetYear <= currentYear) throw new IllegalArgumentException(com.wms.goals.i18n.I18nMessageCollection.VALIDATION_FAILED.name());

        MstCustomerRef cust = common.requireCustomerWithKyc(customerId);
        GoalCommon.RiskProfile rp = common.requireRiskProfile(cust);

        LocalDate now = LocalDate.now();
        LocalDate targetDate = LocalDate.of(targetYear, 12, 31);
        int months = (int) java.time.temporal.ChronoUnit.MONTHS.between(now.withDayOfMonth(1), targetDate.withDayOfMonth(1));
        double i = assumptions.getInflationAnnual()/12d;
        double r = rp.getReturnAnnual()/12d;
        BigDecimal fvGoal = finance.compound(req.getTargetAmount(), i, months);
        BigDecimal pmt = finance.pmt(fvGoal, BigDecimal.ZERO, r, months);
        List<ProjectionPoint> projections = common.buildProjections(now, months, pmt, r, fvGoal);

        MGoal goal = repo.save(MGoal.builder()
                .customerId(customerId)
                .goalType(req.getGoalType())
                .goalName(req.getGoalName())
                .targetAmount(req.getTargetAmount())
                .targetDate(targetDate)
                .riskProfileId(rp.getRiskProfileId())
                .createdAt(java.time.OffsetDateTime.now())
                .build());

        auditProducer.publish("GOAL_CREATED","SUCCESS", customerId, cust.getEmail(), goal.getGoalId(),
                "Goal created",
                Map.of(
                        "returnAnnual", rp.getReturnAnnual(),
                        "inflationAnnual", assumptions.getInflationAnnual(),
                        "compounding", assumptions.getCompounding(),
                        "profile", rp.getProfileType(),
                        "months", months,
                        "targetAmountFuture", fvGoal,
                        "requiredMonthlyContribution", pmt
                ));

        return GoalOtherCreateResponse.builder()
                .goalId(goal.getGoalId())
                .goalType(goal.getGoalType())
                .goalName(goal.getGoalName())
                .targetYear(targetYear)
                .targetAmountPresent(req.getTargetAmount())
                .assumptions(GoalOtherCreateResponse.Assumptions.builder()
                        .returnAnnual(rp.getReturnAnnual())
                        .inflationAnnual(assumptions.getInflationAnnual())
                        .compounding(assumptions.getCompounding())
                        .profile(rp.getProfileType())
                        .targetAmountFuture(fvGoal)
                        .requiredMonthlyContribution(pmt)
                        .monthsInvest(months)
                        .build())
                .projections(projections)
                .build();
    }

    @Transactional(readOnly = true)
    public GoalOtherCreateResponse simulateOther(CreateOtherGoalRequest req, UUID customerId){
        int targetYear = req.getTargetYear();
        int currentYear = LocalDate.now().getYear();
        if (targetYear <= currentYear) throw new IllegalArgumentException(com.wms.goals.i18n.I18nMessageCollection.VALIDATION_FAILED.name());

        MstCustomerRef cust = common.requireCustomerWithKyc(customerId);
        GoalCommon.RiskProfile rp = common.requireRiskProfile(cust);

        LocalDate now = LocalDate.now();
        LocalDate targetDate = LocalDate.of(targetYear, 12, 31);
        int months = (int) java.time.temporal.ChronoUnit.MONTHS.between(now.withDayOfMonth(1), targetDate.withDayOfMonth(1));
        double i = assumptions.getInflationAnnual()/12d;
        double r = rp.getReturnAnnual()/12d;
        BigDecimal fvGoal = finance.compound(req.getTargetAmount(), i, months);
        BigDecimal pmt = finance.pmt(fvGoal, BigDecimal.ZERO, r, months);
        List<ProjectionPoint> projections = common.buildProjections(now, months, pmt, r, fvGoal);

        return GoalOtherCreateResponse.builder()
                .goalId(null)
                .goalType(req.getGoalType())
                .goalName(req.getGoalName())
                .targetYear(targetYear)
                .targetAmountPresent(req.getTargetAmount())
                .assumptions(GoalOtherCreateResponse.Assumptions.builder()
                        .returnAnnual(rp.getReturnAnnual())
                        .inflationAnnual(assumptions.getInflationAnnual())
                        .compounding(assumptions.getCompounding())
                        .profile(rp.getProfileType())
                        .targetAmountFuture(fvGoal)
                        .requiredMonthlyContribution(pmt)
                        .monthsInvest(months)
                        .build())
                .projections(projections)
                .build();
    }

    @Transactional
    public GoalOtherCreateResponse editOther(UUID goalId, UUID customerId, EditOtherGoalRequest req) {
        MGoal g = repo.findById(goalId).orElseThrow(() -> new IllegalArgumentException("GOAL_NOT_FOUND"));
        if (!g.getCustomerId().equals(customerId)) throw new IllegalArgumentException("AUTH_INVALID_CREDENTIALS");

        MstCustomerRef customer = common.requireCustomerWithKyc(customerId);
        GoalCommon.RiskProfile rp = common.requireRiskProfile(customer);

        int targetYear = req.getTargetYear();
        LocalDate now = LocalDate.now();
        if (targetYear <= now.getYear()) throw new IllegalArgumentException(com.wms.goals.i18n.I18nMessageCollection.VALIDATION_FAILED.name());
        LocalDate targetDate = LocalDate.of(targetYear, 12, 31);
        int months = (int) java.time.temporal.ChronoUnit.MONTHS.between(now.withDayOfMonth(1), targetDate.withDayOfMonth(1));

        double i = assumptions.getInflationAnnual()/12d;
        double r = rp.getReturnAnnual()/12d;
        BigDecimal fvGoal = finance.compound(req.getTargetAmount(), i, months);
        BigDecimal pmt = finance.pmt(fvGoal, BigDecimal.ZERO, r, months);
        List<ProjectionPoint> projections = common.buildProjections(now, months, pmt, r, fvGoal);

        g.setTargetAmount(req.getTargetAmount());
        g.setTargetDate(targetDate);
        g.setRiskProfileId(rp.getRiskProfileId());
        g = repo.save(g);

        auditProducer.publish("GOAL_UPDATED","SUCCESS", customerId, customer.getEmail(), g.getGoalId(),
                "Goal OTHER updated",
                Map.of(
                        "returnAnnual", rp.getReturnAnnual(),
                        "inflationAnnual", assumptions.getInflationAnnual(),
                        "compounding", assumptions.getCompounding(),
                        "profile", rp.getProfileType(),
                        "months", months,
                        "targetAmountFuture", fvGoal,
                        "requiredMonthlyContribution", pmt
                ));

        return GoalOtherCreateResponse.builder()
                .goalId(g.getGoalId())
                .goalType(g.getGoalType())
                .goalName(g.getGoalName())
                .targetYear(targetYear)
                .targetAmountPresent(req.getTargetAmount())
                .assumptions(GoalOtherCreateResponse.Assumptions.builder()
                        .returnAnnual(rp.getReturnAnnual())
                        .inflationAnnual(assumptions.getInflationAnnual())
                        .compounding(assumptions.getCompounding())
                        .profile(rp.getProfileType())
                        .targetAmountFuture(fvGoal)
                        .requiredMonthlyContribution(pmt)
                        .monthsInvest(months)
                        .build())
                .projections(projections)
                .build();
    }
}






