package com.healthcare.security_service.controller;

import com.healthcare.security_service.model.LoginRequest;
import com.healthcare.security_service.model.LoginResponse;
import com.healthcare.security_service.service.AuthService;
import com.healthcare.security_service.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = authService.login(
                request.email(),
                request.password()
        );
        return ResponseEntity.ok(new LoginResponse(token));
    }

    // =====================
    // VERIFY TOKEN (API GATEWAY)
    // =====================
    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(
            @RequestHeader("Authorization") String authorization
    ) {
        try {
            log.info("Ingresó al ENDPOIINT. Token: {}"+authorization);
            String token = authorization.replace("Bearer ", "");
            Claims claims = jwtService.validateToken(token);

            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "memberId", claims.get("memberId"),
                    "email", claims.getSubject()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false));
        }
    }
}

