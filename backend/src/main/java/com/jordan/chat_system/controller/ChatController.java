package com.jordan.chat_system.controller;

import com.jordan.chat_system.dto.ChatMessage;
import com.jordan.chat_system.dto.MessageResponse;
import com.jordan.chat_system.dto.SendMessageRequest;
import com.jordan.chat_system.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
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
    }

}
