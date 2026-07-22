package com.jordan.chat_system.repository;

import com.jordan.chat_system.entity.Message;
import com.jordan.chat_system.entity.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("""
    SELECT m
    FROM Message m
    WHERE
        (m.sender.id = :senderId AND m.receiver.id = :receiverId)
        OR
        (m.sender.id = :receiverId AND m.receiver.id = :senderId)
    ORDER BY m.sentAt ASC
""")
    List<Message> findConversation(
            @Param("senderId") Long senderId,
            @Param("receiverId") Long receiverId
    );

    @Query("""
    SELECT m
    FROM Message m
    WHERE m.receiver.email = :email
    AND m.status = :status
""")
    List<Message> findAllByReceiverEmailAndStatus(
            @Param("email") String email,
            @Param("status") MessageStatus status
    );

    @Query("""
    SELECT m
    FROM Message m
    WHERE 
        m.sender.id = :senderId
    AND
        m.receiver.id = :receiverId
    AND
        m.status = :status
""")
    List<Message> findAllBySenderReceiverAndStatus(
            @Param("senderId") Long senderId,
            @Param("receiverId") Long receiverId,
            @Param("status") MessageStatus status
    );
}
