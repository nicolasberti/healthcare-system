package com.healthcare.notification_service.service;

import com.healthcare.notification_service.model.event.Event;
import com.healthcare.notification_service.service.sender.NotificationSender;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService {
    private final List<NotificationSender> notificationSenders;

    public void handle(Event<?> event) {
        notificationSenders.forEach(sender -> sender.send(event));
    }
}
