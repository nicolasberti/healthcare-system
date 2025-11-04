package com.healthcare.notification_service.model.event;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ClaimCreated {
    private String id;
    private String memberId;
    private double amount;
    private String status;
}
