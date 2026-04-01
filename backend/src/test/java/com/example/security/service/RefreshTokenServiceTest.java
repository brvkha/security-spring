package com.example.security.service;

import com.example.security.entity.RefreshToken;
import com.example.security.entity.Role;
import com.example.security.entity.User;
import com.example.security.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RefreshTokenServiceTest {
    private RefreshTokenService service;
    private RefreshTokenRepository repo;

    @BeforeEach
    void setUp() {
        repo = mock(RefreshTokenRepository.class);
        service = new RefreshTokenService(repo);
        ReflectionTestUtils.setField(service, "refreshTokenExpiryMs", 604800000L);
    }

    @Test
    void hashTokenIsConsistent() {
        String raw = "mytoken";
        assertThat(service.hashToken(raw)).isEqualTo(service.hashToken(raw));
    }

    @Test
    void hashTokenDiffersFromRaw() {
        String raw = "mytoken";
        assertThat(service.hashToken(raw)).isNotEqualTo(raw);
    }

    @Test
    void findByRawTokenUsesHash() {
        String raw = service.generateRawToken();
        String hash = service.hashToken(raw);
        RefreshToken token = new RefreshToken();
        token.setTokenHash(hash);
        token.setIssuedAt(Instant.now());
        token.setExpiresAt(Instant.now().plusSeconds(100));
        when(repo.findByTokenHash(hash)).thenReturn(Optional.of(token));

        Optional<RefreshToken> result = service.findByRawToken(raw);
        assertThat(result).isPresent();
        verify(repo).findByTokenHash(hash);
    }
}
