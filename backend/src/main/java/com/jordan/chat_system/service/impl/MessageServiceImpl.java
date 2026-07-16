package com.jordan.chat_system.service.impl;

import com.jordan.chat_system.dto.MessageResponse;
import com.jordan.chat_system.dto.SendMessageRequest;
import com.jordan.chat_system.entity.Message;
import com.jordan.chat_system.entity.User;
import com.jordan.chat_system.exception.ResourceNotFoundException;
import com.jordan.chat_system.repository.MessageRepository;
import com.jordan.chat_system.repository.UserRepository;
import com.jordan.chat_system.service.MessageService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public MessageResponse sendMessage(
            String senderEmail,
            SendMessageRequest request
    ) {

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado"));

        User receiver = userRepository.findById(request.receiverId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Destinatario no encontrado"));

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(request.content())
                .sentAt(LocalDateTime.now())
                .build();

        Message saved = messageRepository.save(message);

        return new MessageResponse(
                saved.getId(),
                saved.getSender().getEmail(),
                saved.getReceiver().getEmail(),
                saved.getContent(),
                saved.getSentAt()
        );
    }

    @Override
    public List<MessageResponse> getConversation(Long senderId, Long receiverId) {
        List<Message> messages = messageRepository.findConversation(
                senderId,
                receiverId
        );

        return messages.stream()
                .map(message -> new MessageResponse(
                        message.getId(),
                        message.getSender().getEmail(),
                        message.getReceiver().getEmail(),
                        message.getContent(),
                        message.getSentAt()
                )).toList();

    }
}
