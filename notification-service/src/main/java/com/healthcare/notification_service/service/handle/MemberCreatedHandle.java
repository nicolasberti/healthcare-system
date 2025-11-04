package com.healthcare.notification_service.service.handle;

import com.healthcare.notification_service.model.MemberContact;
import com.healthcare.notification_service.model.Notification;
import com.healthcare.notification_service.model.event.Event;
import com.healthcare.notification_service.model.event.MemberCreated;
import com.healthcare.notification_service.service.MemberContactService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MemberCreatedHandle implements Handle {
    private final MemberContactService memberContactService;

    @Override
    public MemberContact getMemberContact(Event<?> event) {
        Event<MemberCreated> eventCasted = (Event<MemberCreated>) event;
        return memberContactService.save(eventCasted);
    }

    @Override
    public Notification getNotification() {
        return Notification.builder().message("Bienvenido! Miembro creado").build();
    }
}
