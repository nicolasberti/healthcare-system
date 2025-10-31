package com.healthcare.claim_service.common.command;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreateClaim {
    private String policyId;
    private double amount;
    private String status;
}
