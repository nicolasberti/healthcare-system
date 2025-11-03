package com.healthcare.notification_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
public class Notification {
    private String message;
    private String receiver;
}
