package com.jordan.chat_system.dto;

import com.jordan.chat_system.entity.NotificationType;

public record NotificationMessage(
        NotificationType type,
        String message,
        String username) { }
