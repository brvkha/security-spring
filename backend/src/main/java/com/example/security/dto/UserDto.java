package com.example.security.dto;
import java.time.Instant;
public class UserDto {
    private Long id;
    private String username;
    private String role;
    private boolean locked;
    private boolean enabled;
    private Instant createdAt;
    public UserDto(Long id, String username, String role, boolean locked, boolean enabled, Instant createdAt) {
        this.id = id; this.username = username; this.role = role;
        this.locked = locked; this.enabled = enabled; this.createdAt = createdAt;
    }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public boolean isLocked() { return locked; }
    public boolean isEnabled() { return enabled; }
    public Instant getCreatedAt() { return createdAt; }
}
