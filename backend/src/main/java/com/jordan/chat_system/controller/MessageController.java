package com.jordan.chat_system.controller;

import com.jordan.chat_system.dto.MessageResponse;
import com.jordan.chat_system.dto.MessageStatusUpdate;
import com.jordan.chat_system.dto.SendMessageRequest;
import com.jordan.chat_system.entity.Message;
import com.jordan.chat_system.entity.User;
import com.jordan.chat_system.service.MessageService;
import com.jordan.chat_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
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

        List<Message> readMessages = messageService.markMessagesAsRead(
                receiverId,
                currentUser.getId()
        );

        for (Message message : readMessages) {

            messagingTemplate.convertAndSendToUser(
                    message.getSender().getEmail(),
                    "/queue/message-status",
                    new MessageStatusUpdate(
                            message.getId(),
                            message.getStatus()
                    )
            );
        }

        return messageService.getConversation(
                currentUser.getId(),
                receiverId
        );
    }

    @PostMapping("/read/{senderId}")
    public void markAsRead(
            Authentication authentication,
            @PathVariable Long senderId
    ) {
        User currentUser = userService.getCurrentUser(
                authentication.getName()
        );

        List<Message> readMessages = messageService.markMessagesAsRead(
                senderId,
                currentUser.getId()
        );

        for (Message message : readMessages) {
            messagingTemplate.convertAndSendToUser(
                    message.getSender().getEmail(),
                    "/queue/message-status",
                    new MessageStatusUpdate(
                            message.getId(),
                            message.getStatus()
                    )
            );
        }
    }
}
