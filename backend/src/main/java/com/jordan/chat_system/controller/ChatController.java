package com.jordan.chat_system.controller;

import com.jordan.chat_system.dto.ChatMessage;
import com.jordan.chat_system.dto.MessageResponse;
import com.jordan.chat_system.dto.SendMessageRequest;
import com.jordan.chat_system.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void sendMessage(ChatMessage chatMessage) {
        SendMessageRequest request = new SendMessageRequest(
                chatMessage.receiverId(),
                chatMessage.content()
        );

        MessageResponse response = messageService.sendMessage(
                chatMessage.senderEmail(),
                request
        );

        messagingTemplate.convertAndSendToUser(
                response.receiver(),
                "/queue/messages",
                response
        );
    }

}
