package com.example.security.service;

import com.example.security.dto.UserDto;
import com.example.security.entity.User;
import com.example.security.exception.NotFoundException;
import com.example.security.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAdminService {
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final AuditLogService auditLogService;

    public UserAdminService(UserRepository userRepository, RefreshTokenService refreshTokenService, AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.auditLogService = auditLogService;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserDto(u.getId(), u.getUsername(), u.getRole().name(), u.isLocked(), u.isEnabled(), u.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto lockUser(Long id, String adminUsername) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        user.setLocked(true);
        userRepository.save(user);
        auditLogService.log("LOCK_USER", adminUsername, user.getUsername(), "SUCCESS", "User locked");
        return new UserDto(user.getId(), user.getUsername(), user.getRole().name(), user.isLocked(), user.isEnabled(), user.getCreatedAt());
    }

    @Transactional
    public UserDto unlockUser(Long id, String adminUsername) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        user.setLocked(false);
        userRepository.save(user);
        auditLogService.log("UNLOCK_USER", adminUsername, user.getUsername(), "SUCCESS", "User unlocked");
        return new UserDto(user.getId(), user.getUsername(), user.getRole().name(), user.isLocked(), user.isEnabled(), user.getCreatedAt());
    }

    @Transactional
    public void revokeUserSessions(Long id, String adminUsername) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        int count = refreshTokenService.revokeAllForUser(user);
        auditLogService.log("REVOKE_SESSIONS", adminUsername, user.getUsername(), "SUCCESS", "Revoked " + count + " sessions");
    }
}
