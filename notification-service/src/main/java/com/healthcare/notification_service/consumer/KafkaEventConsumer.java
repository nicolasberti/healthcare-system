package com.healthcare.notification_service.consumer;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.notification_service.model.event.Event;
import com.healthcare.notification_service.model.event.MemberCreatedEvent;
import com.healthcare.notification_service.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class KafkaEventConsumer {
    private final NotificationService notificationService;

    public KafkaEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Mostrar por pantalla los eventos -> modo debug
    @KafkaListener(topics = "${kafka.member.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeFromMemberTopic(String message) {
        try {
            Object json = objectMapper.readValue(message, Object.class);
            String formattedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            System.out.println("📩 Mensaje recibido desde Kafka:");
            System.out.println(formattedJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "${kafka.claim.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeFromClaimTopic(String message) {
        try {
            Object json = objectMapper.readValue(message, Object.class);
            String formattedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            System.out.println("📩 Mensaje recibido desde Kafka:");
            System.out.println(formattedJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   /* @KafkaListener(topics = "${kafka.member.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeFromMemberTopic(String message) {
        try {
            // Parseamos el mensaje como JSON
            JsonNode root = objectMapper.readTree(message);

            // Obtenemos el tipo de evento
            String type = root.get("type").asText();

            // Obtenemos solo el nodo "data" del mensaje
            JsonNode dataNode = root.get("data");

            // Mapear "data" según el tipo
            Object data = switch (type) {
                case "MemberCreated" -> objectMapper.treeToValue(dataNode, MemberCreatedEvent.class);
                default -> objectMapper.treeToValue(dataNode, Map.class);
            };

            // Creamos el Event con type y data
            Event<Object> event = new Event<>();
            event.setType(type);
            event.setData(data);

            System.out.println("📩 Event type: " + type);
            notificationService.handle(event);

        } catch (Exception e) {
            System.err.println("❌ Error procesando evento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "${kafka.claim.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeFromClaimTopic(String message) {
        try {
            // Parseamos el mensaje como JSON
            JsonNode root = objectMapper.readTree(message);

            // Obtenemos el tipo de evento
            String type = root.get("type").asText();

            // Obtenemos solo el nodo "data" del mensaje
            JsonNode dataNode = root.get("data");

            // Mapear "data" según el tipo
            //Object data = switch (type) {
            //    case "MemberCreated" -> objectMapper.treeToValue(dataNode, MemberCreatedEvent.class);
            //    default -> objectMapper.treeToValue(dataNode, Map.class);
            // };

            // Creamos el Event con type y data
            Event<Object> event = new Event<>();
            event.setType(type);
            event.setData(data);

            System.out.println("📩 Event type: " + type);
            notificationService.handle(event);

        } catch (Exception e) {
            System.err.println("❌ Error procesando evento: " + e.getMessage());
            e.printStackTrace();
        }
    }*/


}

