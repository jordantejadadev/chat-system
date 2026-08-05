package com.jordan.chat_system.controller;

import com.jordan.chat_system.dto.*;
import com.jordan.chat_system.entity.Message;
import com.jordan.chat_system.entity.User;
import com.jordan.chat_system.service.MessageService;
import com.jordan.chat_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public Page<MessageResponse> getConversation(
            Authentication authentication,
            @PathVariable Long receiverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
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

        long unreadCount = messageService.countUnreadMessages(
                receiverId,
                currentUser.getId()
        );

        messagingTemplate.convertAndSendToUser(
                currentUser.getEmail(),
                "/queue/unread-count",
                new UnreadCountUpdate(
                        receiverId,
                        unreadCount
                )
        );

        return messageService.getConversation(
                currentUser.getId(),
                receiverId,
                page,
                size
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

        long unreadCount = messageService.countUnreadMessages(
                         senderId,
                         currentUser.getId()
        );

        messagingTemplate.convertAndSendToUser(
                currentUser.getEmail(),
                "/queue/unread-count",
                new UnreadCountUpdate(
                        senderId,
                        unreadCount
                )
        );
    }

    @DeleteMapping("/{messageId}")
    public void deleteMessage(
            @PathVariable Long messageId,
            Authentication authentication
    ){

        Message message = messageService.deleteMessage(
                messageId,
                authentication.getName()
        );

        MessageDeleted deleted = new MessageDeleted(message.getId());

        messagingTemplate.convertAndSendToUser(
                message.getSender().getEmail(),
                "/queue/message-deleted",
                deleted
        );

        messagingTemplate.convertAndSendToUser(
                message.getReceiver().getEmail(),
                "/queue/message-deleted",
                deleted
        );

    }

    @PatchMapping("/{messageId}")
    public void editMessage(
            @PathVariable Long messageId,
            @RequestBody EditMessageRequest request,
            Authentication authentication
    ) {
        System.out.println("Entrpo al PATCH");
        Message message = messageService.editMessage(
                messageId,
                authentication.getName(),
                request
        );

        MessageEdited edited = new MessageEdited(
                message.getId(),
                message.getContent(),
                message.isEdited()
        );

        messagingTemplate.convertAndSendToUser(
                message.getSender().getEmail(),
                "/queue/message-edited",
                edited
        );

        messagingTemplate.convertAndSendToUser(
                message.getReceiver().getEmail(),
                "/queue/message-edited",
                edited
        );
    }

    @GetMapping("/search")
    public List<MessageResponse> searchConversation(
            @RequestParam Long contactId,
            @RequestParam String query,
            Authentication authentication
    ){
        User currentUser = userService.getCurrentUser(
                authentication.getName()
        );

        return messageService.searchConversation(
                currentUser.getId(),
                contactId,
                query
        );
    }
}
