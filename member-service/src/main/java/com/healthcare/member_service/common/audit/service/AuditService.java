package com.healthcare.member_service.common.audit.service;

import com.healthcare.member_service.common.audit.model.AuditEvent;
import com.healthcare.member_service.infrastructure.publisher.sqs.SqsProducer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuditService {
    public SqsProducer sqsProducer;

    @Async("auditExecutor")
    public void audit(String type, String payload) {
        AuditEvent auditEvent = AuditEvent.builder()
                                    .id(UUID.randomUUID().toString())
                                    .type(type)
                                    .payload(payload)
                                    .timestamp(Instant.now())
                                    .version("v1")
                                    .build();
        sqsProducer.send(auditEvent);
    }
}
