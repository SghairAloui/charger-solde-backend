package com.chargersolde.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageDTO {
    private Long id;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String content;
    private boolean isRead;
    private LocalDateTime createdAt;
}
