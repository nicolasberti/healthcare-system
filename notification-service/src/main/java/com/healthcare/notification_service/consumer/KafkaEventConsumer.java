package com.healthcare.notification_service.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.notification_service.model.event.ClaimCreated;
import com.healthcare.notification_service.model.event.ClaimUpdated;
import com.healthcare.notification_service.model.event.Event;
import com.healthcare.notification_service.model.event.MemberCreated;
import com.healthcare.notification_service.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KafkaEventConsumer {
    private final NotificationService notificationService;

    public KafkaEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(
            topics = {"${kafka.member.topic}", "${kafka.claim.topic}"},
            groupId = "${kafka.claim.consumer.group-id}"
    )
    public void consume(String message) {
        try {
            System.out.println("Evento: ");
            System.out.println(message);
            JsonNode root = objectMapper.readTree(message);
            String type = root.get("type").asText();
            JsonNode dataNode = root.get("data");
            Object data = switch (type) {
                case "MemberCreated" -> objectMapper.treeToValue(dataNode, MemberCreated.class);
                case "ClaimCreated" -> objectMapper.treeToValue(dataNode, ClaimCreated.class);
                case "ClaimUpdated" -> objectMapper.treeToValue(dataNode, ClaimUpdated.class);
                default -> objectMapper.treeToValue(dataNode, Map.class);
            };
            Event<Object> event = new Event<>();
            event.setType(type);
            event.setData(data);
            notificationService.handle(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}

