package com.chargersolde.dto;

import com.chargersolde.entity.Claim.ClaimStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ClaimDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String subject;
    private String description;
    private ClaimStatus status;
    private String adminResponse;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
