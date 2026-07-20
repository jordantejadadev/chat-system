package com.jordan.chat_system.controller;

import com.jordan.chat_system.dto.UserResponse;
import com.jordan.chat_system.entity.User;
import com.jordan.chat_system.service.OnlineUserService;
import com.jordan.chat_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final OnlineUserService onlineUserService;

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {

        User user = userService.getCurrentUser(
                authentication.getName()
        );

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                onlineUserService.isOnline(user.getEmail())
        );
    }

    @GetMapping
    public List<UserResponse> getAllUsers(Authentication authentication) {
        return userService.getAllUsersExceptCurrent(authentication.getName());
    }
}
