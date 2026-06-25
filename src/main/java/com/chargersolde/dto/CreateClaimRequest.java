package com.chargersolde.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateClaimRequest {
    @NotBlank(message = "Le sujet est obligatoire")
    private String subject;
    
    @NotBlank(message = "La description est obligatoire")
    private String description;
}
