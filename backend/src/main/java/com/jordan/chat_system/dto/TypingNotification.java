package com.jordan.chat_system.dto;

public record TypingNotification(
        String sender,
        boolean typing
) {}
