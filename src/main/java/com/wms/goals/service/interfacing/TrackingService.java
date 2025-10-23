package com.wms.goals.service.interfacing;

import com.wms.goals.dto.response.GoalTrackingItem;

import java.util.List;
import java.util.UUID;

public interface TrackingService {
    List<GoalTrackingItem> trackingForCustomer(UUID customerId);
}
