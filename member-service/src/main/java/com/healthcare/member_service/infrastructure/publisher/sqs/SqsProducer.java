package com.healthcare.member_service.infrastructure.publisher.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.member_service.common.audit.model.AuditEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Slf4j
@Service
public class SqsProducer {
    private final String queueUrl;
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    public SqsProducer(
        @Value("${aws.sqs.audit-queue.url}") String queueUrl,
        SqsClient sqsClient,
        ObjectMapper objectMapper
    ) {
        this.queueUrl = queueUrl;
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    public void send(AuditEvent auditEvent) {
        try {
            String json = objectMapper.writeValueAsString(auditEvent);

            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(json)
                    .build();
            sqsClient.sendMessage(request);
            log.info("[SQS] Message sent! {}", auditEvent.toString());
        } catch (Exception e) {
            log.error("[SQS] Error sending message... {}", auditEvent.toString());
        }
    }
}

