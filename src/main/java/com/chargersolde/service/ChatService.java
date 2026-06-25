package com.chargersolde.service;

import com.chargersolde.dto.MessageDTO;
import com.chargersolde.entity.Message;
import com.chargersolde.entity.User;
import com.chargersolde.repository.MessageRepository;
import com.chargersolde.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // ─── Send a message ───────────────────────────────────────────
    @Transactional
    public MessageDTO sendMessage(String senderEmail, Long receiverId, String content) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Expéditeur introuvable"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Destinataire introuvable: " + receiverId));

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .isRead(false)
                .build();

        Message saved = messageRepository.save(message);
        MessageDTO dto = toDTO(saved);

        // Push via WebSocket to receiver AND sender using Topic-based approach
        // This avoids the Spring Security session propagation issue with convertAndSendToUser
        messagingTemplate.convertAndSend("/topic/messages." + receiver.getId(), dto);
        messagingTemplate.convertAndSend("/topic/messages." + sender.getId(), dto);
        log.info("Message envoyé de {} à {}", senderEmail, receiver.getEmail());
        return dto;
    }

    // ─── Get conversation between two users ──────────────────────
    public List<MessageDTO> getConversation(String email, Long otherId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));
        User other = userRepository.findById(otherId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable: " + otherId));

        return messageRepository.findConversation(user.getId(), other.getId())
         .stream()
         .map(this::toDTO)
         .collect(Collectors.toList());
    }

    // ─── Get all conversations for admin ─────────────────────────
    public List<MessageDTO> getAllMessages() {
        return messageRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ─── Mapper ───────────────────────────────────────────────────
    private MessageDTO toDTO(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getPrenom() + " " + message.getSender().getNom())
                .receiverId(message.getReceiver().getId())
                .content(message.getContent())
                .isRead(message.isRead())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
