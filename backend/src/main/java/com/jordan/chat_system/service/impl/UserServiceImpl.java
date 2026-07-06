package com.jordan.chat_system.service.impl;

import com.jordan.chat_system.entity.User;
import com.jordan.chat_system.exception.ResourceNotFoundException;
import com.jordan.chat_system.repository.UserRepository;
import com.jordan.chat_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getCurrentUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado"));
    }
}
