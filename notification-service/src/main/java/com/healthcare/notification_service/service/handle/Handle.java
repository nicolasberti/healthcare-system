package com.healthcare.notification_service.service.handle;

import com.healthcare.notification_service.model.MemberContact;
import com.healthcare.notification_service.model.Notification;
import com.healthcare.notification_service.model.event.Event;

public interface Handle {
    MemberContact getMemberContact(Event<?> event);
    Notification getNotification();
}
