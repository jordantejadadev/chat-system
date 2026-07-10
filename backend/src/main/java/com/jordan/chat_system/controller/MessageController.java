package com.jordan.chat_system.controller;

import com.jordan.chat_system.dto.MessageResponse;
import com.jordan.chat_system.dto.SendMessageRequest;
import com.jordan.chat_system.entity.User;
import com.jordan.chat_system.service.MessageService;
import com.jordan.chat_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    @PostMapping("/messages")
    public MessageResponse sendMessage(
            Authentication authentication,
            @RequestBody SendMessageRequest request
            ) {
        return messageService.sendMessage(
                authentication.getName(),
                request
        );
    }

    @GetMapping("/conversation/{receiverId}")
    public List<MessageResponse> getConversation(
            Authentication authentication,
            @PathVariable Long receiverId
    ) {
        User currentUser = userService.getCurrentUser(
                authentication.getName()
        );

        return messageService.getConversation(
                currentUser.getId(),
                receiverId
        );
    }
}
