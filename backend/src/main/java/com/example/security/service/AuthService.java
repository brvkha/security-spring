package com.example.security.service;

import com.example.security.dto.AuthResponse;
import com.example.security.dto.LoginRequest;
import com.example.security.entity.RefreshToken;
import com.example.security.entity.User;
import com.example.security.exception.AuthException;
import com.example.security.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;
    private final AuditLogService auditLogService;

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    @Value("${app.cookie.same-site}")
    private String cookieSameSite;

    @Value("${app.jwt.refresh-token-expiry-ms}")
    private long refreshTokenExpiryMs;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtTokenService jwtTokenService, RefreshTokenService refreshTokenService,
                       AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenService = refreshTokenService;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            auditLogService.log("LOGIN", request.getUsername(), null, "FAILURE", "Invalid password");
            throw new AuthException("Invalid credentials");
        }

        if (user.isLocked()) {
            auditLogService.log("LOGIN", request.getUsername(), null, "FAILURE", "Account locked");
            throw new AuthException("Account is locked");
        }

        if (!user.isEnabled()) {
            auditLogService.log("LOGIN", request.getUsername(), null, "FAILURE", "Account disabled");
            throw new AuthException("Account is disabled");
        }

        String[] rawHolder = new String[1];
        RefreshToken refreshToken = refreshTokenService.createRefreshTokenWithRaw(user, rawHolder);
        String accessToken = jwtTokenService.generateAccessToken(user);

        setRefreshCookie(response, rawHolder[0]);

        auditLogService.log("LOGIN", user.getUsername(), null, "SUCCESS", "Login successful");

        return new AuthResponse(accessToken, jwtTokenService.getAccessTokenExpiryMs(), user.getRole().name());
    }

    @Transactional
    public AuthResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        String rawToken = extractRefreshTokenFromCookie(request)
                .orElseThrow(() -> new AuthException("No refresh token"));

        RefreshToken stored = refreshTokenService.findByRawToken(rawToken)
                .orElseThrow(() -> new AuthException("Invalid refresh token"));

        if (!stored.isActive()) {
            auditLogService.log("REFRESH", stored.getUser().getUsername(), null, "FAILURE", "Refresh token inactive");
            throw new AuthException("Refresh token is not active");
        }

        User user = stored.getUser();

        if (user.isLocked()) {
            throw new AuthException("Account is locked");
        }

        String[] rawHolder = new String[1];
        RefreshToken newToken = refreshTokenService.createRefreshTokenWithRaw(user, rawHolder);
        stored.setReplacedByTokenId(newToken.getId());
        refreshTokenService.revokeToken(stored);

        String accessToken = jwtTokenService.generateAccessToken(user);
        setRefreshCookie(response, rawHolder[0]);

        auditLogService.log("REFRESH", user.getUsername(), null, "SUCCESS", "Token rotated");

        return new AuthResponse(accessToken, jwtTokenService.getAccessTokenExpiryMs(), user.getRole().name());
    }

    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        extractRefreshTokenFromCookie(request).ifPresent(rawToken -> {
            refreshTokenService.findByRawToken(rawToken).ifPresent(token -> {
                refreshTokenService.revokeToken(token);
                auditLogService.log("LOGOUT", token.getUser().getUsername(), null, "SUCCESS", "Logged out");
            });
        });
        clearRefreshCookie(response);
    }

    private Optional<String> extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return Optional.empty();
        return Arrays.stream(request.getCookies())
                .filter(c -> "refresh_token".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    private void setRefreshCookie(HttpServletResponse response, String rawToken) {
        String cookieValue = "refresh_token=" + rawToken
                + "; HttpOnly"
                + "; Path=/api/auth"
                + "; Max-Age=" + (refreshTokenExpiryMs / 1000)
                + "; SameSite=" + cookieSameSite
                + (cookieSecure ? "; Secure" : "");
        response.addHeader("Set-Cookie", cookieValue);
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        String cookieValue = "refresh_token="
                + "; HttpOnly"
                + "; Path=/api/auth"
                + "; Max-Age=0"
                + "; SameSite=" + cookieSameSite
                + (cookieSecure ? "; Secure" : "");
        response.addHeader("Set-Cookie", cookieValue);
    }
}
