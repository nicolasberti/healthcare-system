package com.healthcare.claim_service.domain.port.out;

import com.healthcare.claim_service.domain.event.DomainEvent;

public interface DomainEventPublisher {
    void publish(String aggregateType, Object aggregateId, DomainEvent event);
}
