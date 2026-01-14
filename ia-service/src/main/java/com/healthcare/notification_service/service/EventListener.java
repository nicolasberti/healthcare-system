package com.healthcare.notification_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventListener {
    private final SqsClient sqsClient;

    @Value("${aws.sqs.queue-name}")
    private String queueName;

    private String queueUrl;

    @PostConstruct
    void init() {
        queueUrl = sqsClient.getQueueUrl(
                GetQueueUrlRequest.builder()
                        .queueName(queueName)
                        .build()
        ).queueUrl();
    }

    @Scheduled(fixedDelayString = "${aws.sqs.poll-delay-ms:5000}")
    public void poll() {

        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)     // batch
                .waitTimeSeconds(20)         // long polling
                .visibilityTimeout(60)       // tiempo para procesar
                .build();

        List<Message> messages = sqsClient.receiveMessage(request).messages();

        if (messages.isEmpty()) {
            return;
        }

        for (Message message : messages) {
            try {
                processMessage(message);

                deleteMessage(message);

            } catch (Exception e) {
                log.error("Error procesando mensaje {}", message.messageId(), e);
                // No se borra → retry automático
            }
        }
    }

    private void processMessage(Message message) {
        log.info("Mensaje recibido id={} body={}",
                message.messageId(),
                message.body());

        // Acá debería clasificar al documento según el bucket y la key
        // Posterior a eso, debería:
        // 1. Enviar un mensaje a una cola SQS y que el document-service esté escuchando para actualizar la clasificación
        // 2. Conectarse a la DB donde se almacenan los metadatos de los documentos y actualizar la clasificación
    }

    private void deleteMessage(Message message) {
        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build());
    }
    
}