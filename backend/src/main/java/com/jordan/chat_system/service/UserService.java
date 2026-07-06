package com.jordan.chat_system.service;

import com.jordan.chat_system.entity.User;

public interface UserService {

    User getCurrentUser(String email);
}
