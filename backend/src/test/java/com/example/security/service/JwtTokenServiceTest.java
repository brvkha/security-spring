package com.example.security.service;

import com.example.security.entity.Role;
import com.example.security.entity.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenServiceTest {
    private JwtTokenService service;

    @BeforeEach
    void setUp() {
        service = new JwtTokenService();
        ReflectionTestUtils.setField(service, "jwtSecret", "c2VjdXJpdHktc3ByaW5nLXRyYWluaW5nLWxhYi1zdXBlci1zZWNyZXQta2V5LWZvci1qd3QtaG1hYy1zaGEyNTYtYWxnb3JpdGhtLTIwMjQ=");
        ReflectionTestUtils.setField(service, "accessTokenExpiryMs", 900000L);
    }

    private User makeUser() {
        User u = new User();
        u.setId(1L);
        u.setUsername("testuser");
        u.setRole(Role.USER);
        return u;
    }

    @Test
    void generateAndValidateToken() {
        User user = makeUser();
        String token = service.generateAccessToken(user);
        assertThat(token).isNotBlank();
        assertThat(service.validateToken(token)).isTrue();
    }

    @Test
    void extractUsername() {
        User user = makeUser();
        String token = service.generateAccessToken(user);
        assertThat(service.extractUsername(token)).isEqualTo("testuser");
    }

    @Test
    void invalidTokenReturnsFalse() {
        assertThat(service.validateToken("not.a.token")).isFalse();
    }

    @Test
    void claimsContainRole() {
        User user = makeUser();
        String token = service.generateAccessToken(user);
        Claims claims = service.parseToken(token);
        assertThat(claims.get("role", String.class)).isEqualTo("USER");
    }
}
