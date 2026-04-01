package com.example.security.service;

import com.example.security.entity.RefreshToken;
import com.example.security.entity.User;
import com.example.security.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
public class RefreshTokenService {

    @Value("${app.jwt.refresh-token-expiry-ms}")
    private long refreshTokenExpiryMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String generateRawToken() {
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        String raw = generateRawToken();
        RefreshToken token = new RefreshToken();
        token.setTokenHash(hashToken(raw));
        token.setUser(user);
        token.setIssuedAt(Instant.now());
        token.setExpiresAt(Instant.now().plusMillis(refreshTokenExpiryMs));
        return refreshTokenRepository.save(token);
    }

    @Transactional
    public RefreshToken createRefreshTokenWithRaw(User user, String[] rawHolder) {
        String raw = generateRawToken();
        rawHolder[0] = raw;
        RefreshToken token = new RefreshToken();
        token.setTokenHash(hashToken(raw));
        token.setUser(user);
        token.setIssuedAt(Instant.now());
        token.setExpiresAt(Instant.now().plusMillis(refreshTokenExpiryMs));
        return refreshTokenRepository.save(token);
    }

    public Optional<RefreshToken> findByRawToken(String rawToken) {
        String hash = hashToken(rawToken);
        return refreshTokenRepository.findByTokenHash(hash);
    }

    @Transactional
    public void revokeToken(RefreshToken token) {
        token.setRevokedAt(Instant.now());
        refreshTokenRepository.save(token);
    }

    @Transactional
    public int revokeAllForUser(User user) {
        return refreshTokenRepository.revokeAllByUser(user, Instant.now());
    }
}
