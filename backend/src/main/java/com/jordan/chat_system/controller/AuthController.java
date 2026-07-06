package com.jordan.chat_system.controller;

import com.jordan.chat_system.dto.AuthResponse;
import com.jordan.chat_system.dto.LoginRequest;
import com.jordan.chat_system.dto.RegisterRequest;
import com.jordan.chat_system.entity.User;
import com.jordan.chat_system.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {

        User user = authService.register(request);

        return new AuthResponse(
                null,
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
