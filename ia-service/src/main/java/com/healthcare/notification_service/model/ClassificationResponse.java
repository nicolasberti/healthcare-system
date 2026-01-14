package com.healthcare.notification_service.model;

import lombok.*;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class ClassificationResponse {
    private String id;
    private String classification;
}
