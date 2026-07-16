package com.jordan.chat_system.dto;

public record ChatMessage(
        Long receiverId,
        String content
) {}
