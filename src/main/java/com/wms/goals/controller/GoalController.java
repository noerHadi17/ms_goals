package com.wms.goals.controller;

import com.wms.goals.dto.request.CreateOtherGoalRequest;
import com.wms.goals.dto.request.CreateRetirementGoalRequest;
import com.wms.goals.dto.request.EditOtherGoalRequest;
import com.wms.goals.dto.request.EditRetirementGoalRequest;
import com.wms.goals.dto.response.GoalOtherCreateResponse;
import com.wms.goals.dto.response.GoalSummary;
import com.wms.goals.dto.response.GoalTrackingItem;
import com.wms.goals.dto.response.RetirementCreateResponse;
import com.wms.goals.i18n.I18nMessageCollection;
import com.wms.goals.service.interfacing.OtherGoalService;
import com.wms.goals.service.interfacing.RetirementGoalService;
import com.wms.goals.service.interfacing.GoalQueryService;
import com.wms.goals.service.interfacing.TrackingService;
import com.wms.goals.utility.ApiResponseUtil;
import com.wms.goals.web.ResponseWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/v1/goals")
@RequiredArgsConstructor
@Slf4j
public class GoalController {
    private final OtherGoalService otherService;
    private final RetirementGoalService retirementService;
    private final GoalQueryService queryService;
    private final TrackingService trackingService;
    private final MessageSource messageSource;

    @PostMapping("/createdGoalsRetirement")
    public ResponseEntity<ResponseWrapper<RetirementCreateResponse>> createdGoalRetirement(
            @RequestHeader("X-User-Id") UUID customerId,
            @RequestBody @Valid CreateRetirementGoalRequest req,
            Locale locale) {
        RetirementCreateResponse data = retirementService.createRetirement(req, customerId);
        return ResponseEntity.created(java.net.URI.create("/v1/goals/" + data.getGoalId()))
                .body(ApiResponseUtil.success(data, I18nMessageCollection.GOAL_CREATED.name(),
                        I18nMessageCollection.GOAL_CREATED.localized(messageSource, locale)));
    }

    @PostMapping("/createdGoalsOther")
    public ResponseEntity<ResponseWrapper<GoalOtherCreateResponse>> createdGoalOther(
            @RequestHeader("X-User-Id") UUID customerId,
            @RequestBody @Valid CreateOtherGoalRequest req,
            Locale locale) {
        GoalOtherCreateResponse data = otherService.createOther(req, customerId);
        return ResponseEntity.created(java.net.URI.create("/v1/goals/" + data.getGoalId()))
                .body(ApiResponseUtil.success(data, I18nMessageCollection.GOAL_CREATED.name(),
                        I18nMessageCollection.GOAL_CREATED.localized(messageSource, locale)));
    }

    @PostMapping("/simulateCreatedGoalsOther")
    public ResponseEntity<ResponseWrapper<GoalOtherCreateResponse>> simulateOther(
            @RequestHeader("X-User-Id") UUID customerId,
            @RequestBody @Valid CreateOtherGoalRequest req,
            Locale locale) {
        GoalOtherCreateResponse data = otherService.simulateOther(req, customerId);
        return ResponseEntity.ok(ApiResponseUtil.success(data, I18nMessageCollection.SIMULATION_DONE.name(),
                I18nMessageCollection.SIMULATION_DONE.localized(messageSource, locale)));
    }

    @PostMapping("/simulateCreatedGoalsRetirement")
    public ResponseEntity<ResponseWrapper<RetirementCreateResponse>> simulateRetirement(
            @RequestHeader("X-User-Id") UUID customerId,
            @RequestBody @Valid CreateRetirementGoalRequest req,
            Locale locale) {
        RetirementCreateResponse data = retirementService.simulateRetirement(req, customerId);
        return ResponseEntity.ok(ApiResponseUtil.success(data, I18nMessageCollection.SIMULATION_DONE.name(),
                I18nMessageCollection.SIMULATION_DONE.localized(messageSource, locale)));
    }

    @GetMapping("/listGoals")
    public ResponseEntity<ResponseWrapper<java.util.List<GoalSummary>>> listGoals(
            @RequestHeader("X-User-Id") UUID customerId,
            Locale locale) {
        java.util.List<GoalSummary> data = queryService.list(customerId);
        return ResponseEntity.ok(ApiResponseUtil.success(data, I18nMessageCollection.GOAL_LIST_FETCHED.name(),
                messageSource.getMessage("GOAL_LIST_FETCHED", null, locale)));
    }

    @GetMapping("/detailGoals/{goalId}")
    public ResponseEntity<ResponseWrapper<GoalSummary>> detailGoals(
            @RequestHeader("X-User-Id") UUID customerId,
            @PathVariable("goalId") UUID goalId,
            Locale locale) {
        GoalSummary data = queryService.get(customerId, goalId);
        return ResponseEntity.ok(ApiResponseUtil.success(data, I18nMessageCollection.GOAL_DETAIL_FETCHED.name(),
                messageSource.getMessage("GOAL_DETAIL_FETCHED", null, locale)));
    }

    @GetMapping("/trackingGoals")
    public ResponseEntity<ResponseWrapper<java.util.List<GoalTrackingItem>>> trackingGoals(
            @RequestHeader("X-User-Id") UUID customerId,
            Locale locale){
        java.util.List<GoalTrackingItem> data = trackingService.trackingForCustomer(customerId);
        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        data,
                        I18nMessageCollection.PROJECTION_DONE.name(),
                        messageSource.getMessage("PROJECTION_DONE", null, locale)
                )
        );
    }

    @PutMapping("/editGoalsOther/{goalId}")
    public ResponseEntity<ResponseWrapper<GoalOtherCreateResponse>> editGoalsOther(
            @RequestHeader("X-User-Id") UUID customerId,
            @PathVariable("goalId") UUID goalId,
            @RequestBody @Valid EditOtherGoalRequest req,
            Locale locale) {
        GoalOtherCreateResponse data = otherService.editOther(goalId, customerId, req);
        return ResponseEntity.ok(ApiResponseUtil.success(data, I18nMessageCollection.GOAL_UPDATED.name(),
                messageSource.getMessage("GOAL_UPDATED", null, locale)));
    }

    @PutMapping("/editGoalsRetirement/{goalId}")
    public ResponseEntity<ResponseWrapper<RetirementCreateResponse>> editGoalsRetirement(
            @RequestHeader("X-User-Id") UUID customerId,
            @PathVariable("goalId") UUID goalId,
            @RequestBody @Valid EditRetirementGoalRequest req,
            Locale locale) {
        RetirementCreateResponse data = retirementService.editRetirement(goalId, customerId, req);
        return ResponseEntity.ok(ApiResponseUtil.success(data, I18nMessageCollection.GOAL_UPDATED.name(),
                messageSource.getMessage("GOAL_UPDATED", null, locale)));
    }
}
