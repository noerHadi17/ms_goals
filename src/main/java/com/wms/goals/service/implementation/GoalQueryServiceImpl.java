package com.wms.goals.service.implementation;

import com.wms.goals.dto.response.GoalSummary;
import com.wms.goals.entity.MGoal;
import com.wms.goals.repository.GoalRepository;
import com.wms.goals.service.interfacing.GoalQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoalQueryServiceImpl implements GoalQueryService {
    private final GoalRepository repo;

    @Override
    public List<GoalSummary> list(UUID customerId) {
        return repo.findAllByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(g -> GoalSummary.builder()
                        .goalId(g.getGoalId())
                        .goalType(g.getGoalType())
                        .goalName(g.getGoalName())
                        .targetAmount(g.getTargetAmount())
                        .targetDate(g.getTargetDate())
                        .riskProfileId(g.getRiskProfileId())
                        .createdAt(g.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    public GoalSummary get(UUID customerId, UUID goalId) {
        MGoal g = repo.findById(goalId).orElseThrow(() -> new IllegalArgumentException("GOAL_NOT_FOUND"));
        if (!g.getCustomerId().equals(customerId)) throw new IllegalArgumentException("AUTH_INVALID_CREDENTIALS");
        return GoalSummary.builder()
                .goalId(g.getGoalId())
                .goalType(g.getGoalType())
                .goalName(g.getGoalName())
                .targetAmount(g.getTargetAmount())
                .targetDate(g.getTargetDate())
                .riskProfileId(g.getRiskProfileId())
                .createdAt(g.getCreatedAt())
                .build();
    }
}
