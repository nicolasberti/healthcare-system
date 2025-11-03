package com.healthcare.notification_service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "${kafka.member.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        try {
            // Convertir el string JSON a un objeto genérico (para pretty print)
            Object json = objectMapper.readValue(message, Object.class);
            String formattedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);

            System.out.println("📩 Mensaje recibido desde Kafka:");
            System.out.println(formattedJson);

        } catch (Exception e) {
            System.err.println("❌ Error procesando el mensaje: " + e.getMessage());
        }
    }
}

