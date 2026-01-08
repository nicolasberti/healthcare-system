package com.healthcare.member_service.infrastructure.publisher.kafka.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.healthcare.member_service.domain.event.DomainEvent;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonFormat;

import java.net.URI;
import java.util.UUID;

public class EventMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(JsonFormat.getCloudEventJacksonModule()); // ← CRÍTICO

    public static CloudEvent domainEventToCloudEvent(
            DomainEvent event,
            String aggregateType,
            Object aggregateId
    ) {
        try {
            String eventType = event.getClass().getSimpleName();
            String source = "member-service";

            byte[] data = objectMapper
                    .writeValueAsString(event)
                    .getBytes(java.nio.charset.StandardCharsets.UTF_8);

            return CloudEventBuilder.v1()
                    .withId(UUID.randomUUID().toString())
                    .withSource(URI.create("https://" + source + "/" + aggregateType + "/" + aggregateId))
                    .withType(eventType)
                    .withDataContentType("application/json")
                    .withExtension("aggregatetype", aggregateType)
                    .withExtension("aggregateid", aggregateId.toString())
                    .withData(data)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error mapping domain event to CloudEvent: " + e.getMessage(), e);
        }
    }

    public static String cloudEventToJson(CloudEvent cloudEvent) {
        try {
            return objectMapper.writeValueAsString(cloudEvent);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing CloudEvent to JSON: " + e.getMessage(), e);
        }
    }
}