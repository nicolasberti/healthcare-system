package com.healthcare.notification_service.service.handle;

import com.healthcare.notification_service.model.MemberContact;
import com.healthcare.notification_service.model.Notification;
import com.healthcare.notification_service.model.event.ClaimCreated;
import com.healthcare.notification_service.model.event.Event;
import com.healthcare.notification_service.model.event.MemberCreated;
import com.healthcare.notification_service.service.MemberContactService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ClaimCreatedHandle implements Handle {
    private final MemberContactService memberContactService;

    @Override
    public MemberContact getMemberContact(Event<?> event) {
        Event<ClaimCreated> eventCasted = (Event<ClaimCreated>) event;
        return memberContactService.getById(eventCasted.getData().getMemberId());
    }

    @Override
    public Notification getNotification() {
        return Notification.builder().message("Se creo un nuevo reclamo, te avisaremos cuando se actualice").build();
    }
}
