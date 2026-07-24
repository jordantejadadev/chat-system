package com.jordan.chat_system.dto;

public record UnreadCountUpdate(
        Long senderId,
        long unreadCount
) {}
