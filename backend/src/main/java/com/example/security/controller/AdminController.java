package com.example.security.controller;

import com.example.security.dto.AuditLogDto;
import com.example.security.dto.UserDto;
import com.example.security.service.AuditLogService;
import com.example.security.service.UserAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {
    private final UserAdminService userAdminService;
    private final AuditLogService auditLogService;

    public AdminController(UserAdminService userAdminService, AuditLogService auditLogService) {
        this.userAdminService = userAdminService;
        this.auditLogService = auditLogService;
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users")
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok(userAdminService.getAllUsers());
    }

    @PatchMapping("/users/{id}/lock")
    @Operation(summary = "Lock a user")
    public ResponseEntity<UserDto> lockUser(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(userAdminService.lockUser(id, principal.getName()));
    }

    @PatchMapping("/users/{id}/unlock")
    @Operation(summary = "Unlock a user")
    public ResponseEntity<UserDto> unlockUser(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(userAdminService.unlockUser(id, principal.getName()));
    }

    @PostMapping("/users/{id}/sessions/revoke")
    @Operation(summary = "Revoke all sessions for a user")
    public ResponseEntity<Void> revokeSessions(@PathVariable Long id, Principal principal) {
        userAdminService.revokeUserSessions(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/audit-logs")
    @Operation(summary = "Get audit logs")
    public ResponseEntity<Page<AuditLogDto>> getAuditLogs(@PageableDefault(size = 20) Pageable pageable) {
        Page<AuditLogDto> page = auditLogService.getLogs(pageable)
                .map(log -> new AuditLogDto(log.getId(), log.getEventType(), log.getActor(),
                        log.getTarget(), log.getTimestamp(), log.getResult(), log.getDetails()));
        return ResponseEntity.ok(page);
    }
}
