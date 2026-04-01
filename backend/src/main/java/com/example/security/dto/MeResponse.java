package com.example.security.dto;
public class MeResponse {
    private String username;
    private String role;
    private boolean locked;
    public MeResponse(String username, String role, boolean locked) {
        this.username = username; this.role = role; this.locked = locked;
    }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public boolean isLocked() { return locked; }
}
