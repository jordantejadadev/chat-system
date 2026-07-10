package com.jordan.chat_system.service;

import com.jordan.chat_system.dto.MessageResponse;
import com.jordan.chat_system.dto.SendMessageRequest;

import java.util.List;

public interface MessageService {

    MessageResponse sendMessage(
            String senderEmail,
            SendMessageRequest request
    );

    List<MessageResponse> getConversation(Long senderId, Long receiverId);
}
