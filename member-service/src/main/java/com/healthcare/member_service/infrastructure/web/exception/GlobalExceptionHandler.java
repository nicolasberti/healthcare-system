package com.healthcare.member_service.infrastructure.web.exception;

import com.healthcare.member_service.domain.model.member.exception.MemberAlreadyExistsException;
import com.healthcare.member_service.domain.model.member.exception.MemberNotFoundException;
import com.healthcare.member_service.infrastructure.web.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 409 Conflict → cuando ya existe
    @ExceptionHandler(MemberAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleMemberAlreadyExists(MemberAlreadyExistsException ex) {
        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(false)
                .data(null)
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // 404 Not Found → cuando no existe
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleMemberNotFound(MemberNotFoundException ex) {
        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(false)
                .data(null)
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 500 Internal Server Error → cualquier otra excepción
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(false)
                .data(null)
                .message("Unexpected error: " + ex.getMessage())
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

