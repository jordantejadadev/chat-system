package com.jordan.chat_system.service;

import java.util.Set;

public interface OnlineUserService {

    void connect(String email);

    void disconnect(String email);

    Set<String> getOnlineUsers();

    boolean isOnline(String email);
}
