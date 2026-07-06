package com.jordan.chat_system.dto;

public record AuthResponse(
        String token,
        String username,
        String email,
        String role
) {
}