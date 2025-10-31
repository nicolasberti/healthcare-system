package com.healthcare.claim_service.infrastructure.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@Table(name = "claim")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ClaimEntity {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "policy_id")
    private String policyId;

    @Column(name = "amount")
    private double amount;

    @Column(name = "status")
    private String status;
}
