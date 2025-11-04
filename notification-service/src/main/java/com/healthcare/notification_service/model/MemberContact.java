package com.healthcare.notification_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_contact")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberContact {
    @Id
    private String id;
    private String email;
    private String phone;
}
