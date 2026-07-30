package com.jordan.chat_system.dto;

public record ReplyMessage(
        Long id,
        String content,
        String sender
) {}
