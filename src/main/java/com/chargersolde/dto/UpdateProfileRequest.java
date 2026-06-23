package com.chargersolde.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequest {


    @NotBlank
    private String nom;


    @NotBlank
    private String prenom;


    private String numTel;


}