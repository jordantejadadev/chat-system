package com.jordan.chat_system.service;

import com.jordan.chat_system.dto.AuthResponse;
import com.jordan.chat_system.dto.LoginRequest;
import com.jordan.chat_system.dto.RegisterRequest;
import com.jordan.chat_system.entity.User;

public interface AuthService {

    User register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
