package com.healthcare.notification_service.service.sender;

import com.healthcare.notification_service.model.event.Event;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationSender implements NotificationSender {
    @Override
    public void send(Event<?> event) {
        System.out.println("📩 logica para procesar el envio de notificaciones por notificaciones push...");
    }
}
