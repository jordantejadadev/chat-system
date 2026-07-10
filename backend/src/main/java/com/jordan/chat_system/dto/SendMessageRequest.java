package com.jordan.chat_system.dto;

public record SendMessageRequest(
        Long receiverId,
        String content
) {
}
