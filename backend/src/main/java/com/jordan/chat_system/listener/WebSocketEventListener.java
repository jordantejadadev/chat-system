package com.jordan.chat_system.listener;

import com.jordan.chat_system.dto.MessageStatusUpdate;
import com.jordan.chat_system.entity.Message;
import com.jordan.chat_system.service.MessageService;
import com.jordan.chat_system.service.OnlineUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final OnlineUserService onlineUserService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        Authentication authentication = (Authentication) accessor.getUser();

        if (authentication != null) {
            onlineUserService.disconnect(authentication.getName());

            messagingTemplate.convertAndSend(
                    "/topic/online-users",
                    onlineUserService.getOnlineUsers()
            );
        }
    }

    @EventListener
    public void handleConnect(SessionConnectedEvent event) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        Authentication authentication = (Authentication) accessor.getUser();

        System.out.println(authentication.getName());

        if(authentication != null) {

            onlineUserService.connect(authentication.getName());

            List<Message> deliveredMessages = messageService.markMessagesAsDelivered(
                    authentication.getName()
            );

            for (Message message : deliveredMessages) {
                messagingTemplate.convertAndSendToUser(
                        message.getSender().getEmail(),
                        "/queue/message-status",
                        new MessageStatusUpdate(
                                message.getId(),
                                message.getStatus()
                        )
                );
            }

            messagingTemplate.convertAndSend(
                    "/topic/online-users",
                    onlineUserService.getOnlineUsers()
            );
        }
    }
}
