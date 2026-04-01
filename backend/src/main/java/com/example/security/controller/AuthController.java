package com.example.security.controller;

import com.example.security.config.RateLimitConfig;
import com.example.security.dto.AuthResponse;
import com.example.security.dto.LoginRequest;
import com.example.security.service.AuthService;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Authentication endpoints")
public class AuthController {
    private final AuthService authService;
    private final RateLimitConfig rateLimitConfig;

    public AuthController(AuthService authService, RateLimitConfig rateLimitConfig) {
        this.authService = authService;
        this.rateLimitConfig = rateLimitConfig;
    }

    @PostMapping("/login")
    @Operation(summary = "Login and get access + refresh tokens")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
                                   HttpServletRequest httpRequest,
                                   HttpServletResponse httpResponse) {
        String ip = getClientIp(httpRequest);
        Bucket bucket = rateLimitConfig.resolveLoginBucket(ip);
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(java.util.Map.of("error", "Too many requests"));
        }
        AuthResponse auth = authService.login(request, httpResponse);
        return ResponseEntity.ok(auth);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token using HttpOnly cookie")
    public ResponseEntity<?> refresh(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        String ip = getClientIp(httpRequest);
        Bucket bucket = rateLimitConfig.resolveRefreshBucket(ip);
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(java.util.Map.of("error", "Too many requests"));
        }
        AuthResponse auth = authService.refresh(httpRequest, httpResponse);
        return ResponseEntity.ok(auth);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout and revoke refresh token")
    public ResponseEntity<Void> logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        authService.logout(httpRequest, httpResponse);
        return ResponseEntity.noContent().build();
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
