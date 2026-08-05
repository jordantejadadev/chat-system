package com.jordan.chat_system.service;

import com.jordan.chat_system.dto.EditMessageRequest;
import com.jordan.chat_system.dto.MessageResponse;
import com.jordan.chat_system.dto.SendMessageRequest;
import com.jordan.chat_system.entity.Message;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MessageService {

    MessageResponse sendMessage(
            String senderEmail,
            SendMessageRequest request
    );

    Page<MessageResponse> getConversation(Long senderId, Long receiverId, int page, int size);

    List<Message> markMessagesAsDelivered(String receiverEmail);

    List<Message> markMessagesAsRead(Long senderId, Long receiverId);

    long countUnreadMessages(
            Long senderId,
            Long receiverId
    );

    Message deleteMessage(
            Long messageId,
            String currentUserEmail
    );

    Message editMessage(
            Long messageId,
            String currentUserEmail,
            EditMessageRequest request
    );

    List<MessageResponse> searchConversation(
            Long userId,
            Long contactId,
            String query
    );
}
