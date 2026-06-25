package com.chargersolde.repository;

import com.chargersolde.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT m FROM Message m WHERE (m.sender.id = :user1 AND m.receiver.id = :user2) OR (m.sender.id = :user2 AND m.receiver.id = :user1) ORDER BY m.createdAt ASC")
    List<Message> findConversation(@org.springframework.data.repository.query.Param("user1") Long user1, @org.springframework.data.repository.query.Param("user2") Long user2);
}
