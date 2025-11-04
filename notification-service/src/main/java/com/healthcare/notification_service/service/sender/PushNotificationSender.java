package com.healthcare.notification_service.service.sender;

import com.healthcare.notification_service.model.MemberContact;
import com.healthcare.notification_service.model.Notification;
import com.healthcare.notification_service.model.event.Event;
import com.healthcare.notification_service.model.event.MemberCreated;
import com.healthcare.notification_service.service.handle.Handle;
import com.healthcare.notification_service.service.handle.HandleRegistry;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PushNotificationSender implements NotificationSender {
    private final HandleRegistry handleRegistry;

    @Override
    public void send(Event<?> event) {
        Handle handle = handleRegistry.getHandler(event.getType());
        MemberContact memberContact = handle.getMemberContact(event);
        Notification notification = handle.getNotification();

        System.out.println("📩 [Phone "+memberContact.getPhone()+"] sending push notification... Message: " + notification.getMessage());
    }

}
