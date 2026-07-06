package com.jordan.chat_system.dto;

public record UserResponse(
        String username,
        String email,
        String role
) {
}
