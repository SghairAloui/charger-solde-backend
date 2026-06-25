package com.chargersolde.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatMessageRequest {
    @NotNull(message = "Le destinataire est obligatoire")
    private Long receiverId;
    
    @NotBlank(message = "Le message ne peut pas être vide")
    private String content;
}
