package com.jordan.chat_system.controller;

import com.jordan.chat_system.dto.AuthResponse;
import com.jordan.chat_system.dto.LoginRequest;
import com.jordan.chat_system.dto.NotificationMessage;
import com.jordan.chat_system.dto.RegisterRequest;
import com.jordan.chat_system.entity.User;
import com.jordan.chat_system.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {

        User user = authService.register(request);

        messagingTemplate.convertAndSend(
                "/topic/users-updated",
                "refresh"
        );

        return new AuthResponse(
                user.getId(),
                null,
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

        messagingTemplate.convertAndSend(
                "/topic/user-notifications",
                new NotificationMessage(
                        "LOGIN",
                        response.username() + " se ha conectado",
                        response.username()
                )
        );

        return response;
    }
}
