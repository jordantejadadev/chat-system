package com.jordan.chat_system.dto;

public record ChatMessage(
        String senderEmail,
        Long receiverId,
        String content
) {}
