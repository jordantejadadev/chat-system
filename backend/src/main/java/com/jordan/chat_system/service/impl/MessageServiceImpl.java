package com.jordan.chat_system.service.impl;

import com.jordan.chat_system.dto.MessageResponse;
import com.jordan.chat_system.dto.SendMessageRequest;
import com.jordan.chat_system.entity.Message;
import com.jordan.chat_system.entity.MessageStatus;
import com.jordan.chat_system.entity.User;
import com.jordan.chat_system.exception.ResourceNotFoundException;
import com.jordan.chat_system.repository.MessageRepository;
import com.jordan.chat_system.repository.UserRepository;
import com.jordan.chat_system.service.MessageService;
import com.jordan.chat_system.service.OnlineUserService;
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
    private final OnlineUserService onlineUserService;

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

        MessageStatus status = onlineUserService.isOnline(receiver.getEmail())
                ? MessageStatus.DELIVERED
                : MessageStatus.SENT;

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(request.content())
                .sentAt(LocalDateTime.now())
                .status(status)
                .build();

        Message saved = messageRepository.save(message);

        return new MessageResponse(
                saved.getId(),
                saved.getSender().getId(),
                saved.getReceiver().getId(),
                saved.getSender().getEmail(),
                saved.getReceiver().getEmail(),
                saved.getContent(),
                saved.getSentAt(),
                saved.getStatus()
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
                        message.getSender().getId(),
                        message.getReceiver().getId(),
                        message.getSender().getEmail(),
                        message.getReceiver().getEmail(),
                        message.getContent(),
                        message.getSentAt(),
                        message.getStatus()
                )).toList();

    }

    @Override
    public List<Message> markMessagesAsDelivered(String receiverEmail) {

        List<Message> messages = messageRepository.findAllByReceiverEmailAndStatus(
                receiverEmail,
                MessageStatus.SENT
        );

        for (Message message : messages) {
            message.setStatus(MessageStatus.DELIVERED);
        }

        return messageRepository.saveAll(messages);
    }

    @Override
    public List<Message> markMessagesAsRead(
            Long senderId,
            Long receiverId
    ) {

        List<Message> messages = messageRepository.findAllBySenderReceiverAndStatus(
                senderId,
                receiverId,
                MessageStatus.DELIVERED
        );

        for (Message message : messages) {
            message.setStatus(MessageStatus.READ);
        }

        return messageRepository.saveAll(messages);
    }


}
