package com.healthcare.notification_service.service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final S3Service s3Service;

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    // Los eventos son disparados por S3 Event Notification al momento de subir un archivo. El formato es el definido por AWS. Consultar docu.
    //@Scheduled(fixedDelayString = "${aws.sqs.poll-delay-ms:5000}")
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
                // No se borra -> retry automático
            }
        }
    }

    private void processMessage(Message message) {
        log.info("Mensaje recibido id={} body={}",
                message.messageId(),
                message.body());

        try {
            JsonNode root = objectMapper.readTree(message.body());

            JsonNode records = root.get("Records");
            if (records == null || !records.isArray() || records.isEmpty()) {
                throw new IllegalArgumentException("Mensaje inválido: no contiene Records");
            }

            // En general viene 1 solo record, pero S3 siempre manda array
            JsonNode record = records.get(0);

            String bucket = record
                    .path("s3")
                    .path("bucket")
                    .path("name")
                    .asText(null);

            String key = record
                    .path("s3")
                    .path("object")
                    .path("key")
                    .asText(null);

            if (bucket == null || key == null) {
                throw new IllegalArgumentException("Mensaje inválido: bucket o key faltante");
            }

            // IMPORTANTE: el key puede venir URL-encoded
            key = URLDecoder.decode(key, StandardCharsets.UTF_8);

            String classification = s3Service.classify(bucket, key);

            log.info(
                    "Se clasificó automáticamente un documento en S3. [{}, {}, {}]",
                    bucket, key, classification
            );

            /* TODO:
                Una vez obtenida la clasificación:
                1. Guardar en los metadatos del documento (accediendo directamente a la DB)
                2. Desacoplar el proceso y enviar un mensaje mediante otra SQS a document-service
            */
           
        } catch (Exception e) {
            throw new RuntimeException("Error procesando mensaje SQS", e);
        }
    }


    private void deleteMessage(Message message) {
        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build());
    }
    
}