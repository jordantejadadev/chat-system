package com.jordan.chat_system.dto;

import com.jordan.chat_system.entity.MessageStatus;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        Long senderId,
        Long receiverId,
        String sender,
        String receiver,
        String content,
        LocalDateTime sentAt,
        MessageStatus status
) {}

