package com.wms.goals.service.interfacing;

import com.wms.goals.dto.response.GoalSummary;

import java.util.List;
import java.util.UUID;

public interface GoalQueryService {
    List<GoalSummary> list(UUID customerId);
    GoalSummary get(UUID customerId, UUID goalId);
}
