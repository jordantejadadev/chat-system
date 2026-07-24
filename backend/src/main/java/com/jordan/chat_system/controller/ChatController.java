package com.jordan.chat_system.controller;

import com.jordan.chat_system.dto.*;
import com.jordan.chat_system.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void sendMessage(Principal principal, ChatMessage chatMessage) {

        System.out.println("Mensaje recibido por websocket");

        SendMessageRequest request = new SendMessageRequest(
                chatMessage.receiverId(),
                chatMessage.content()
        );

        MessageResponse response = messageService.sendMessage(
                principal.getName(),
                request
        );

        messagingTemplate.convertAndSendToUser(
                response.receiver(),
                "/queue/messages",
                response
        );

        messagingTemplate.convertAndSendToUser(
                response.sender(),
                "/queue/messages",
                response
        );

        long unreadCount = messageService.countUnreadMessages(
                response.senderId(),
                response.receiverId()
        );

        messagingTemplate.convertAndSendToUser(
                response.receiver(),
                "/queue/unread-count",
                new UnreadCountUpdate(
                        response.senderId(),
                        unreadCount
                )
        );
    }

    @MessageMapping("/chat/typing")
    public void typing(
            Principal principal,
            TypingRequest request
    ) {
        messagingTemplate.convertAndSendToUser(
                request.receiver(),
                "/queue/typing",
                new TypingNotification(
                        principal.getName(),
                        request.typing()
                )
        );
    }

}
