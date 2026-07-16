package com.jordan.chat_system.service;

import com.jordan.chat_system.dto.UserResponse;
import com.jordan.chat_system.entity.User;

import java.util.List;

public interface UserService {

    User getCurrentUser(String email);

    List<UserResponse> getAllUsersExceptCurrent(String currentEmail);
}
