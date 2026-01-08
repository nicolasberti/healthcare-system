package com.healthcare.member_service.common.audit.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auditable {
    String type();   // ej: MEMBER_CREATED
}
