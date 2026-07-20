package com.jordan.chat_system.service.impl;

import com.jordan.chat_system.dto.UserResponse;
import com.jordan.chat_system.entity.User;
import com.jordan.chat_system.exception.ResourceNotFoundException;
import com.jordan.chat_system.repository.UserRepository;
import com.jordan.chat_system.service.OnlineUserService;
import com.jordan.chat_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OnlineUserService onlineUserService;

    @Override
    public User getCurrentUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado"));
    }

    @Override
    public List<UserResponse> getAllUsersExceptCurrent(String currentEmail) {
        return userRepository.findByEmailNot(currentEmail).stream()
                .map(u -> new UserResponse(
                        u.getId(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getRole().name(),
                        onlineUserService.isOnline(u.getEmail())
                ))
                .toList();
    }
}
