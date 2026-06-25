package com.chargersolde.controller;

import com.chargersolde.dto.ApiResponse;
import com.chargersolde.dto.ChatMessageRequest;
import com.chargersolde.dto.MessageDTO;
import com.chargersolde.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Tag(name = "Messagerie", description = "Chat en temps réel via WebSocket + REST")
@SecurityRequirement(name = "BearerAuth")
public class ChatController {

    private final ChatService chatService;

    // ── WebSocket: send message (STOMP /app/chat.send) ────────────
    @MessageMapping("/chat.send")
    public void handleWebSocketMessage(@Payload ChatMessageRequest request, Principal principal) {
        chatService.sendMessage(principal.getName(), request.getReceiverId(), request.getContent());
    }

    // ── REST: Send message (HTTP fallback) ────────────────────────
    @PostMapping("/api/messages")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Envoyer un message")
    public ResponseEntity<ApiResponse<MessageDTO>> sendMessage(
            Authentication auth,
            @Valid @RequestBody ChatMessageRequest request) {

        MessageDTO dto = chatService.sendMessage(auth.getName(), request.getReceiverId(), request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Message envoyé", dto));
    }

    // ── REST: Get conversation ────────────────────────────────────
    @GetMapping("/api/messages/conversation/{userId}")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Conversation avec un utilisateur")
    public ResponseEntity<ApiResponse<List<MessageDTO>>> getConversation(
            Authentication auth,
            @PathVariable Long userId) {

        return ResponseEntity.ok(ApiResponse.success("Conversation récupérée",
                chatService.getConversation(auth.getName(), userId)));
    }

    // ── ADMIN: Get all messages ───────────────────────────────────
    @GetMapping("/api/messages")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tous les messages (Admin)")
    public ResponseEntity<ApiResponse<List<MessageDTO>>> getAllMessages() {
        return ResponseEntity.ok(ApiResponse.success("Messages récupérés",
                chatService.getAllMessages()));
    }
}
