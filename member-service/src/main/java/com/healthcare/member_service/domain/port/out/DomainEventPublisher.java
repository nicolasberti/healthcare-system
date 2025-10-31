package com.healthcare.member_service.domain.port.out;

import com.healthcare.member_service.domain.event.DomainEvent;

import java.util.List;

public interface DomainEventPublisher {
    void publish(String aggregateType, Object aggregateId, DomainEvent event);
}
