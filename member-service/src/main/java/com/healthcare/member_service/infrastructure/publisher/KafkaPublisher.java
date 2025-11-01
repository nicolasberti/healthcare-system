package com.healthcare.member_service.infrastructure.publisher;

import com.healthcare.member_service.domain.event.DomainEvent;
import com.healthcare.member_service.domain.port.out.DomainEventPublisher;

public class KafkaPublisher implements DomainEventPublisher {

    @Override
    public void publish(String aggregateType, Object aggregateId, DomainEvent event) {

    }
}
