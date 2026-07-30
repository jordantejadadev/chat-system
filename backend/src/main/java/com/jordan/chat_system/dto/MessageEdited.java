package com.jordan.chat_system.dto;

public record MessageEdited(
        Long messageId,
        String content,
        boolean edited
) {}
