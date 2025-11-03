package com.healthcare.member_service.infrastructure.publisher;

import com.healthcare.member_service.domain.event.DomainEvent;
import com.healthcare.member_service.domain.port.out.DomainEventPublisher;
import com.healthcare.member_service.infrastructure.publisher.mapper.EventMapper;
import io.cloudevents.CloudEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPublisher implements DomainEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.member.topic}")
    private String topic;

    public KafkaPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(String aggregateType, Object aggregateId, DomainEvent event) {
        try {
            CloudEvent cloudEvent = EventMapper.domainEventToCloudEvent(event, aggregateType, aggregateId);
            String json = EventMapper.cloudEventToJson(cloudEvent);
            kafkaTemplate.send(topic, aggregateId.toString(), json);
        } catch (Exception e) {
            throw new RuntimeException("Error publishing event to Kafka: " + e.getMessage(), e);
        }
    }
}