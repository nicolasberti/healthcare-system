package com.healthcare.security_service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.security_service.model.AuditEvent;
import com.healthcare.security_service.repository.AuditEventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

@Service
@Slf4j
public class SqsConsumer {
    private final String queueUrl;
    private final AuditEventRepository repository;
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    public SqsConsumer(
        @Value("${aws.sqs.audit-queue.url}") String queueUrl,
        AuditEventRepository repository,
        SqsClient sqsClient,
        ObjectMapper objectMapper
    ) {
        this.queueUrl = queueUrl;
        this.repository = repository;
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 5000)
    public void pollMessages() {

        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(5)
                .waitTimeSeconds(10) // long polling
                .build();

        ReceiveMessageResponse response = sqsClient.receiveMessage(request);

        for (Message message : response.messages()) {
            try {
                AuditEvent auditEvent =
                        objectMapper.readValue(message.body(), AuditEvent.class);
                log.info("📥 AuditEvent recibido: type={}, id={}",
                        auditEvent.getType(), auditEvent.getId());
                process(auditEvent);
                deleteMessage(message);
            } catch (Exception e) {
                log.error("❌ Error procesando mensaje SQS", e);
                // NO borrar → reintento / DLQ
            }
        }
    }

    private void process(AuditEvent event) {
        log.info("Saving audit event... {}", event.toString());
        repository.save(event);
    }

    private void deleteMessage(Message message) {
        DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build();
        sqsClient.deleteMessage(deleteRequest);
    }
}

