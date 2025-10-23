package com.wms.goals.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.audit:audit.events}")
    private String auditTopic;

    public void publish(String action, String status, UUID customerId, String email, UUID goalId, String description, Map<String,Object> meta) {
        AuditEvent evt = AuditEvent.builder()
                .eventId(UUID.randomUUID())
                .occurredAt(OffsetDateTime.now().toString())
                .serviceName("goals-service")
                .action(action)
                .status(status)
                .customerId(customerId)
                .goalId(goalId)
                .email(email)
                .description(description)
                .meta(meta)
                .build();
        kafkaTemplate.send(auditTopic, goalId != null ? goalId.toString() : null, evt)
                .whenComplete((r, ex) -> {
                    if (ex == null) log.info("Audit published action={} goalId={}", action, goalId);
                    else log.warn("Audit publish failed action={} err={}", action, ex.getMessage());
                });
    }
}
