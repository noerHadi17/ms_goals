package com.wms.goals.service.interfacing;

import com.wms.goals.dto.request.CreateRetirementGoalRequest;
import com.wms.goals.dto.request.EditRetirementGoalRequest;
import com.wms.goals.dto.response.RetirementCreateResponse;

import java.util.UUID;

public interface RetirementGoalService {
    RetirementCreateResponse createRetirement(CreateRetirementGoalRequest req, UUID customerId);
    RetirementCreateResponse simulateRetirement(CreateRetirementGoalRequest req, UUID customerId);
    RetirementCreateResponse editRetirement(UUID goalId, UUID customerId, EditRetirementGoalRequest req);
}
