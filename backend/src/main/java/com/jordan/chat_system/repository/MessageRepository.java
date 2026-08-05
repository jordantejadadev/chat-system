package com.jordan.chat_system.repository;

import com.jordan.chat_system.entity.Message;
import com.jordan.chat_system.entity.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    ORDER BY m.sentAt DESC
""")
    Page<Message> findConversation(
            @Param("senderId") Long senderId,
            @Param("receiverId") Long receiverId,
            Pageable pageable
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

    @Query("""
    SELECT COUNT(m)
    FROM Message m
    WHERE
        m.sender.id = :senderId
    AND
        m.receiver.id = :receiverId
    AND
        m.status <> 'READ'
""")
    long countUnreadMessages(
            @Param("senderId") Long senderId,
            @Param("receiverId") Long receiverId
    );

    @Query("""
    SELECT m
    FROM Message m
    WHERE (
        (m.sender.id = :userId AND m.receiver.id = :contactId)
        OR
        (m.sender.id = :contactId AND m.receiver.id = : userId)
    )
    AND LOWER(m.content) LIKE LOWER(CONCAT('%', :query, '%'))
    ORDER BY m.sentAt ASC
""")
    List<Message> searchConversation(
            @Param("userId") Long userId,
            @Param("contactId") Long contactId,
            @Param("query") String query
    );
}
