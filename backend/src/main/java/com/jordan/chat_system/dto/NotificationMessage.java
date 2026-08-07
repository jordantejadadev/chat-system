package com.jordan.chat_system.dto;

public record NotificationMessage(
        String type,
        String message,
        String username) { }
