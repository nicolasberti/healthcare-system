package com.healthcare.security_service.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class AuditEvent {
    private String id;
    private String type;
    private String payload;
    private Instant timestamp;
    private String version;

    public String toString() {
        return "AuditEvent [id=" + id + ", type=" + type + ", payload=" + payload + ", timestamp=" + timestamp + ", version=" + version + "]";
    }
}
