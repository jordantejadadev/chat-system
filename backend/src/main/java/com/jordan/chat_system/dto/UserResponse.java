package com.jordan.chat_system.dto;

public record UserResponse(
        Long id,
        String username,
        String email,
        String role,
        boolean online
) {
}
