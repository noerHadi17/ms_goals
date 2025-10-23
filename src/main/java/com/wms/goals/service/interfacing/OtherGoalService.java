package com.wms.goals.service.interfacing;

import com.wms.goals.dto.request.CreateOtherGoalRequest;
import com.wms.goals.dto.request.EditOtherGoalRequest;
import com.wms.goals.dto.response.GoalOtherCreateResponse;

import java.util.UUID;

public interface OtherGoalService {
    GoalOtherCreateResponse createOther(CreateOtherGoalRequest req, UUID customerId);
    GoalOtherCreateResponse simulateOther(CreateOtherGoalRequest req, UUID customerId);
    GoalOtherCreateResponse editOther(UUID goalId, UUID customerId, EditOtherGoalRequest req);
}
