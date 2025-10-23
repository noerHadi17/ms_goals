package com.wms.goals.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEvent {
    private UUID eventId;
    private String occurredAt;
    private String serviceName;
    private String action;
    private String status;
    private UUID customerId;
    private UUID goalId;
    private String email;
    private String description;
    private Map<String,Object> meta;
}
