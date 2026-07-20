package com.jordan.chat_system.service.impl;

import com.jordan.chat_system.service.OnlineUserService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OnlineUserServiceImpl implements OnlineUserService {

    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

    @Override
    public void connect(String email) {
        onlineUsers.add(email);
        System.out.println(onlineUsers);
    }

    @Override
    public void disconnect(String email) {
        onlineUsers.remove(email);
        System.out.println(onlineUsers);
    }

    @Override
    public Set<String> getOnlineUsers() {
        return onlineUsers;
    }

    @Override
    public boolean isOnline(String email) {
        return onlineUsers.contains(email);
    }
}
