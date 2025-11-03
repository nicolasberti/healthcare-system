package com.healthcare.notification_service.service.sender;

import com.healthcare.notification_service.model.Notification;
import com.healthcare.notification_service.model.event.Event;
import com.healthcare.notification_service.model.event.MemberCreatedEvent;
import org.springframework.stereotype.Service;

@Service
public class EmailSender implements NotificationSender {
    @Override
    public void send(Event<?> event) {
        System.out.println("📩 logica para procesar el envio de notificaciones por email..." + getNotification(event).getMessage());
    }

    private Notification getNotification(Event<?> event) {
        return switch (event.getType()) {
            case "MemberCreated" -> handleMemberCreated((Event<MemberCreatedEvent>) event);
            default -> null;
        };
    }

    private Notification handleMemberCreated(Event<MemberCreatedEvent> event) {
        MemberCreatedEvent data = event.getData();
        return Notification.builder()
                .message("Prueba mensaje email")
                //.receiver(data.ge)
                .build();
    }
}
