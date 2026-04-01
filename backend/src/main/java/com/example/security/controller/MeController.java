package com.example.security.controller;

import com.example.security.dto.MeResponse;
import com.example.security.entity.User;
import com.example.security.exception.NotFoundException;
import com.example.security.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@Tag(name = "User", description = "User endpoints")
@SecurityRequirement(name = "bearerAuth")
public class MeController {
    private final UserRepository userRepository;

    public MeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user info")
    public ResponseEntity<MeResponse> me(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));
        return ResponseEntity.ok(new MeResponse(user.getUsername(), user.getRole().name(), user.isLocked()));
    }
}
