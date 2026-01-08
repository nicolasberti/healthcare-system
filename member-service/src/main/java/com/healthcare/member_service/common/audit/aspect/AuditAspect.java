package com.healthcare.member_service.common.audit.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.member_service.common.audit.service.AuditService;
import com.healthcare.member_service.common.audit.annotation.Auditable;
import com.healthcare.member_service.common.audit.model.AuditPayload;
import com.healthcare.member_service.common.audit.model.ErrorPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        Object result;

        try {
            result = joinPoint.proceed();

            AuditPayload payload = new AuditPayload(
                    joinPoint.getArgs(),
                    result
            );

            String jsonPayload = objectMapper.writeValueAsString(payload);

            auditService.audit(
                    auditable.type(),
                    jsonPayload
            );

            return result;

        } catch (Throwable ex) {
            ErrorPayload errorPayload = new ErrorPayload(
                    joinPoint.getArgs(),
                    ex.getMessage()
            );

            String jsonPayload = objectMapper.writeValueAsString(errorPayload);

            auditService.audit(
                    auditable.type() + "_ERROR",
                    jsonPayload
            );

            throw ex;
        }
    }
}

