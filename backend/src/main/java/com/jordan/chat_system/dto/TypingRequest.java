package com.jordan.chat_system.dto;

public record TypingRequest(
        String receiver,
        boolean typing) {}
