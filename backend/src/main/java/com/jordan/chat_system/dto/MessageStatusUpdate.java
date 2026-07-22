package com.jordan.chat_system.dto;

import com.jordan.chat_system.entity.MessageStatus;

public record MessageStatusUpdate(
        Long messageId,
        MessageStatus status
) {}
