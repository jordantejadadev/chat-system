package com.jordan.chat_system.dto;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        String sender,
        String receiver,
        String content,
        LocalDateTime sentAt
) {}

