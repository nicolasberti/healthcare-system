package com.healthcare.notification_service.service.handle;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class HandleRegistry {
    private final Map<String, Handle> handlers = new HashMap<>();

    public HandleRegistry(
            MemberCreatedHandle memberCreatedHandle,
            ClaimCreatedHandle claimCreatedHandle,
            ClaimUpdatedHandle claimUpdatedHandle
    ) {
        handlers.put("MemberCreated", memberCreatedHandle);
        handlers.put("ClaimCreated", claimCreatedHandle);
        handlers.put("ClaimUpdated", claimUpdatedHandle);
    }

    public Handle getHandler(String eventType) {
        return handlers.get(eventType);
    }
}

