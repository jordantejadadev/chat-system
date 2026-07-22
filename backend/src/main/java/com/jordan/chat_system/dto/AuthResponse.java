package com.jordan.chat_system.dto;

public record AuthResponse(
        Long id,
        String token,
        String username,
        String email,
        String role
) {
}