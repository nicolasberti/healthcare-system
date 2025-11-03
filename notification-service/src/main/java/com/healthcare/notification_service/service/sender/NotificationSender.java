package com.healthcare.notification_service.service.sender;

import com.healthcare.notification_service.model.Notification;
import com.healthcare.notification_service.model.event.Event;

public interface NotificationSender {
    void send(Event<?> event);
}
