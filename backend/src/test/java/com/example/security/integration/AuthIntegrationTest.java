package com.example.security.integration;

import com.example.security.entity.Role;
import com.example.security.entity.User;
import com.example.security.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        if (!userRepository.existsByUsername("testuser")) {
            User u = new User();
            u.setUsername("testuser");
            u.setPassword(passwordEncoder.encode("password123"));
            u.setRole(Role.USER);
            userRepository.save(u);
        }
        if (!userRepository.existsByUsername("testadmin")) {
            User a = new User();
            a.setUsername("testadmin");
            a.setPassword(passwordEncoder.encode("admin123"));
            a.setRole(Role.ADMIN);
            userRepository.save(a);
        }
    }

    @Test
    void loginSuccess() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("username", "testuser", "password", "password123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void loginFailure() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("username", "testuser", "password", "wrongpassword"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void meEndpointRequiresAuth() throws Exception {
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void meEndpointWithValidToken() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("username", "testuser", "password", "password123"))))
                .andReturn();
        String body = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(body).get("accessToken").asText();

        mockMvc.perform(get("/api/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void adminEndpointDeniedForUser() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("username", "testuser", "password", "password123"))))
                .andReturn();
        String body = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(body).get("accessToken").asText();

        mockMvc.perform(get("/api/admin/users").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpointAllowedForAdmin() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("username", "testadmin", "password", "admin123"))))
                .andReturn();
        String body = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(body).get("accessToken").asText();

        mockMvc.perform(get("/api/admin/users").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void refreshRotatesToken() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("username", "testuser", "password", "password123"))))
                .andReturn();
        
        String setCookie = loginResult.getResponse().getHeader("Set-Cookie");
        assertThat(setCookie).contains("refresh_token=");
        
        String rawToken = setCookie.split("refresh_token=")[1].split(";")[0];

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/refresh")
                .cookie(new jakarta.servlet.http.Cookie("refresh_token", rawToken)))
                .andReturn();
        assertThat(refreshResult.getResponse().getStatus()).isEqualTo(200);
        
        String newCookie = refreshResult.getResponse().getHeader("Set-Cookie");
        assertThat(newCookie).contains("refresh_token=");
        String newToken = newCookie.split("refresh_token=")[1].split(";")[0];
        assertThat(newToken).isNotEqualTo(rawToken);
    }

    @Test
    void lockUserPreventsLogin() throws Exception {
        // Login as admin
        MvcResult adminLogin = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("username", "testadmin", "password", "admin123"))))
                .andReturn();
        String adminToken = objectMapper.readTree(adminLogin.getResponse().getContentAsString())
                .get("accessToken").asText();

        // Get user ID
        MvcResult usersResult = mockMvc.perform(get("/api/admin/users")
                .header("Authorization", "Bearer " + adminToken))
                .andReturn();
        com.fasterxml.jackson.databind.JsonNode users = objectMapper.readTree(usersResult.getResponse().getContentAsString());
        Long userId = null;
        for (com.fasterxml.jackson.databind.JsonNode u : users) {
            if ("testuser".equals(u.get("username").asText())) {
                userId = u.get("id").asLong();
                break;
            }
        }
        assertThat(userId).isNotNull();

        // Lock user
        mockMvc.perform(patch("/api/admin/users/" + userId + "/lock")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locked").value(true));

        // Try to login as locked user
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("username", "testuser", "password", "password123"))))
                .andExpect(status().isUnauthorized());
    }
}
